const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.checkPinEvents = functions.https.onRequest((req, res) => {

  //create database refs
  var pinsRef = admin.database().ref('/pins');
  var eventsRef = admin.database().ref('/events');
  var foodRef = admin.database().ref('/food');

  eventsRef.once('value', (snapshot) => {
  	snapshot.forEach((childSnapshot) => {
  		var data = childSnapshot.val();
  		try {
  			var timeStart = data.timeStart;
  			var duration = data.duration;
  			var curTime = new Date().getTime();

  			// if event is expired
  			if(timeStart + duration < curTime){
  				var eventKey = childSnapshot.key;
  				var pinId = data.pinId;

          // removed food associated with expired event
          foodRef.orderByChild('eventId').equalTo(eventKey).once('value').then((foodSnapshot) => {
            return foodSnapshot.forEach((foodChildSnapshot) => {
              var foodKey = foodChildSnapshot.key;
              console.log('Removed food item ' + foodKey + ' for expired event ' + eventKey);
              return foodRef.child(foodKey).remove();
            });
          }).catch((err) => {
            console.log(err);
          });

  				// remove expired event, decrement numEvents on associated pin
  				pinsRef.child(pinId).child('numEvents').transaction((numEvents) => {
  					eventsRef.child(eventKey).remove();
  					if(numEvents) {
  						numEvents--;
  						console.log('Expired event ' + eventKey + ' for pin ' + pinId +
  						'. This pin now has ' + numEvents + ' event(s).');
  					}
  					return numEvents;
  				});
  			}
  		}
  		catch(err) {
  			console.log(err);
			res.status(404).end();
  		}
  	})
  })

  //send back response
  res.status(200).end();

});

exports.sendNotificationsForEventAdded = functions.database.ref('/events/{eventId}')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const event = snapshot.val();
      const eventId = context.params.eventId;
      console.log('New event added', eventId, event);

      // TODO Get the list of device notification tokens.
      const getUsersPromise = admin.database()
          .ref('/users').once('value');

      return Promise.all([getUsersPromise]).then(results => {
        let users = results[0].val();

        // Notification details.
        const payload = {
          notification: {
            title: 'Free food added in your area!',
            body: eventId
          }
        };

        // TODO: Currently sends to all users with a device token for Firebase Cloud Messaging
        // Once we implement preferences we should only send to users with preferences that
        // match the event
        let tokens = [];
        for (let userId in users) {
          if (users.hasOwnProperty(userId)) {
            let token = users[userId].instanceId;
            if (token) {
              tokens.push(token);
            }
          }
        }
        console.log('Will send to these device tokens: ', tokens);

        // Send notifications to all tokens.
        return admin.messaging().sendToDevice(tokens, payload);
      }).then((response) => {
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', tokens[index], error);
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          }
        });
        return Promise.all(tokensToRemove);
      });
    });


exports.createEventFromEmail = functions.https.onRequest((req, res) => {
    const express = require('express');
    const bodyParser = require('body-parser');
    var app = express();
    var port = 5000;

    app.use(bodyParser.json());

    app.set('port', (process.env.PORT || port));

    app.use(express.static(__dirname + '/public'));

    app.get('/', function(request, response) {
      var Imap = require('imap'),
        inspect = require('util').inspect;
      var buffer = '';

      var myMap;

      var imap = new Imap({
        user: "free.stanfood@gmail.com",
        password: "stanfoodalpaca",
        host: "imap.gmail.com", //this may differ if you are using some other mail services like yahoo
        port: 993,
        tls: true,
        connTimeout: 10000, // Default by node-imap
        authTimeout: 5000, // Default by node-imap,
        debug: console.log, // Or your custom function with only one incoming argument. Default: null
        tlsOptions: { rejectUnauthorized: false },
        mailbox: "INBOX", // mailbox to monitor
        searchFilter: ["UNSEEN", "FLAGGED"], // the search filter being used after an IDLE notification has been retrieved
        markSeen: true, // all fetched email will be marked as seen and not fetched next time
        fetchUnreadOnStart: true, // use it only if you want to get all unread email on lib start. Default is `false`,
        mailParserOptions: { streamAttachments: false }, // options to be passed to mailParser lib.
        attachments: false, // download attachments as they are encountered to the project directory
        attachmentOptions: { directory: "attachments/" } // specify a download directory for attachments
      });

      function openInbox(cb) {
        imap.openBox('INBOX', false, cb);
      }

      imap.once('ready', function () {
        openInbox(function (err, box) {
          if (err) throw err;
          imap.search(['UNSEEN', ['TO', 'Give Subject Here']], function (err, results) {
            if (err) throw err;
            var f = imap.fetch(results, { bodies: ['HEADER.FIELDS (SUBJECT)','TEXT'], markSeen: true });
            f.on('message', function (msg, seqno) {
              console.log('Message #%d' + seqno);
              console.log('Message type' + msg.text)
              var prefix = '(#' + seqno + ') ';
              msg.on('body', function (stream, info) {
                if (info.which === 'TEXT')
                  console.log(prefix + 'Body [%s] found, %d total bytes', inspect(info.which), info.size);
                stream.on('data', function (chunk) {
                  buffer += chunk.toString('utf8');
                  console.log("BUFFER" + buffer)
                  if (info.which === 'TEXT')
                    console.log(prefix + 'Body [%s] (%d/%d)', inspect(info.which), info.size);
                })
                stream.once('end', function () {
                  if (info.which !== 'TEXT')
                    console.log(prefix + 'Parsed header: %s', inspect(Imap.parseHeader(buffer)));
                  else
                    console.log(prefix + 'Body [%s] Finished', inspect(info.which));
                  console.log("BUFFER" + buffer)
                });
              });
              msg.once('attributes', function (attrs) {
                console.log(prefix + 'Attributes: %s', inspect(attrs, false, 8));
              });
              msg.once('end', function () {
                console.log(prefix + 'Finished');
              });
            });
            f.once('error', function (err) {
              console.log('Fetch error: ' + err);
            });
            f.once('end', function () {
              console.log('Done fetching all messages!');
              imap.end();
            });
          });
        });
      });

      imap.once('error', function (err) {
        console.log(err);
      });

      imap.once('end', function () {
        console.log('Connection ended');
      });

      imap.connect();
    });
});
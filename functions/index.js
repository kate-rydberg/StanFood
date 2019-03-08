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

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

/**
 *  The Big Switch(es)
 *
 *  Author: SmartThings
 *  Modified: Brandon Gordon
 *
 *  Date: 2013-05-01
 *
 *
 *  Added support for multiple master switches
 *  Only respond to physical button presses
 *
 */
definition(
	name: "The Big Switches - Physical",
	namespace: "notoriousbdg",
	author: "SmartThings",
	description: "Turns on and off a collection of lights based on the state of a specific switch.",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
	section("When this switch is turned on or off") {
		input "master", "capability.switch", title: "Where?", multiple: true
	}
	section("Turn on or off all of these switches as well") {
		input "switches", "capability.switch", multiple: true, required: false
	}
	section("And turn off but not on all of these switches") {
		input "offSwitches", "capability.switch", multiple: true, required: false
	}
	section("And turn on but not off all of these switches") {
		input "onSwitches", "capability.switch", multiple: true, required: false
	}
}

def installed()
{
	subscribe(master, "switch.on", onHandler)
	subscribe(master, "switch.off", offHandler)
}

def updated()
{
	unsubscribe()
	installed()
}

def onHandler(evt) {
	log.debug evt.value
	if (evt.isPhysical()) {
        if(switches)    {
        	log.debug switches
			switches?.on()
		}
        if(onSwitches)  {
        	log.debug onSwitches
        	onSwitches?.on()
        }
    }
}

def offHandler(evt) {
	log.debug evt.value
	if (evt.isPhysical()) {
        if(switches)    {
        	log.debug switches
        	switches?.off()
        }
        if(offSwitches) {
        	log.debug offSwitches
        	offSwitches?.off()
        }
    }
}

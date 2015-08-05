def smartAppNameFull() {
	return	"Fan Sync SmartApp for SmartThings"
}

def smartAppNameShort() {
	return	"Fan Sync"
}

def smartAppVersion() {
	return	"Version 0.1.0"
}

def smartAppAuthor() {
    return  "Author Brandon Gordon"
}

def smartAppCopyright() {
    return  "Copyright (c) 2014 Brandon Gordon"
}

def smartAppSource() {
	return	"https://github.com/notoriousbdg/SmartThings.FanSync"
}

def smartAppDescription() {
	return	"This SmartThings SmartApp will run thermostat fans when selected switches are on. " +
			"This can be useful to supplement bathroom fans, redistribute fireplace heat, and more."
}

def smartAppRevision () {
    return  '2014-11-14  v0.0.1\n' +
            ' * Initial release\n'
}

def smartAppLicense() {
    return  'Licensed under the Apache License, Version 2.0 (the "License"); you ' +
            'may not use this file except in compliance with the License. You ' +
            'may obtain a copy of the License at:' +
            '\n\n' +
            'http://www.apache.org/licenses/LICENSE-2.0' +
            '\n\n' +
            'Unless required by applicable law or agreed to in writing, software ' +
            'distributed under the License is distributed on an "AS IS" BASIS, ' +
            'WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or ' +
            'implied. See the License for the specific language governing ' +
            'permissions and limitations under the License.'
}

definition(
	name: "Fan Sync",
    namespace: "notoriousbdg",
    author: "Brandon Gordon",
	description: "This SmartThings SmartApp will run thermostat fans when selected switches are on. This can be useful to supplement bathroom fans, redistribute fireplace heat, and more.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
    page name:"pageStatus"
    page name:"pageConfigure"
    page name:"pageAbout"
}

// Show About Page
def pageAbout() {
    def pageProperties = [
        name:           "pageAbout",
        title:          smartAppNameFull(),
        nextPage:       "pageConfigure",
        uninstall:      true
    ]

    return dynamicPage(pageProperties) {
        section() {
            paragraph smartAppVersion() + "\n" +
                      smartAppAuthor() + "\n" +
                      smartAppCopyright()
        }
        
        section() {
            paragraph smartAppDescription()
        }
        
        section() {
            href(
                name: "sourceCode",
                title: "Source Code (Tap to view)",
                required: false,
                external: true,
                style: "external",
                url: smartAppSource(),
                description: smartAppSource()
            )
        }

        section() {
            paragraph title: "Revision History",
                      smartAppRevision()
        }
        
        section() {
            paragraph title: "License",
                      smartAppLicense()
        }  
    }
}

// Show Status page
def pageStatus() {
    def pageProperties = [
        name:       "pageStatus",
        title:      smartAppNameShort() + " Status",
        nextPage:   null,
        install:    true,
        uninstall:  true
    ]

	if (settings.switches == null) {
        return pageAbout()
    }
    
	
    
    return dynamicPage(pageProperties) {
		def listSwitchStatus	 = ""
		def listThermostatStatus = ""
        def listSettings         = ""
		
		settings.switches.each() {
			listSwitchStatus += "${it.displayName} is ${it.currentValue("switch")}\n"
		}

		settings.thermostat.each() {
			//it.poll()
			listThermostatStatus += "${it.displayName} fan is ${it.currentValue("thermostatFanMode")}\n"
		}
        
        listSettings += "Fan will run for ${settings.delayOffMinutes} minutes after all switches are turned off\n\n"
        if (settings.pushMessage) {
        	listSettings += "Push notifications are enabled\n\n"
        }
        if (settings.phoneNumber != null) {
        	listSettings += "SMS notifications are enabled for ${settings.phoneNumber}\n\n"
        }

		if (listSwitchStatus) {
			section("Switch Status") {
				paragraph listSwitchStatus.trim()
            }
        }

		if (listThermostatStatus) {
			section("Thermostat Status") {
				paragraph listThermostatStatus.trim()
            }
        }

		if (listSettings) {
			section("Settings") {
				paragraph listSettings.trim()
            }
        }

        section("Menu") {
            href "pageStatus", title:"Refresh", description:""
            href "pageConfigure", title:"Configure", description:""
            href "pageAbout", title:"About", description: ""
        }
    }
}

// Show Configure Page
def pageConfigure() {
    def helpPage =
		"Select switches and thermostats."

	def inputSwitch = [
		name:			"switches",
		type:			"capability.switch",
		title:			"Which switches?",
        multiple:       true,
        required:       true
    ]

	def inputThermostat = [
		name:			"thermostat",
		type:			"capability.thermostat",
		title:			"Which thermostat fans should be toggled with the above switches?",
		multiple:		true,
        required:       true
    ]

	def inputDelayOffMinutes = [
		name:			"delayOffMinutes",
        type:           "number",
		title:			"Run thermostat fan for how many minutes after all switches are turned off?",
		defaultValue:	0,
        required:       true
    ]

    def inputPush      = [
        name:           "pushMessage",
        type:           "bool",
		title:			"Send Push notifications",
		defaultValue:	false
    ]

    def inputSMS       = [
        name:           "phoneNumber",
        type:           "phone",
		title:			"Send SMS notification",
        required:       false
    ]

    def pageProperties = [
        name:           "pageConfigure",
        title:          smartAppNameShort() + " Configuration",
        nextPage:       "pageStatus",
        uninstall:      true
    ]

    return dynamicPage(pageProperties) {
        section("About") {
            paragraph helpPage
        }

		section("Input Switches") {
			input inputSwitch
		}
		
		section("Target Thermostat Fans") {
			input inputThermostat
        }
        
        section("Settings") {
			input inputDelayOffMinutes
        }
        
        section("Notification") {
            input inputPush
            input inputSMS
        }

        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
        }
    }
}

def installed() {
    log.debug "Initialized with settings: ${settings}"

    initialize()
}

def updated() {
    unschedule()
    unsubscribe()
    initialize()
}

def initialize() {
	log.debug "Initialized with settings: ${settings}"
	
	subscribe(switches, "switch.on",  switchHandlerOn)
	subscribe(switches, "switch.off", switchHandlerOff)

	runIn(60, updateStatus)
}

def send(msg) {
    log.debug msg

    if (settings.pushMessage) {
        sendPush(msg)
    } else {
        sendNotificationEvent(msg)
    }

    if (settings.phoneNumber != null) {
        sendSms(phoneNumber, msg) 
    }
}

def updateStatus() {
	def fanOn = false
	
	unschedule()

	settings.switches.each() {
		if (it.currentValue("switch") == "on") {
			fanOn = true
		}
	}
	
	if (fanOn) {
		log.debug "Setting thermostat fan to on"
		send "Setting thermostat fan to on"
		thermostat*.fanOn()
	} else {
		log.debug "Setting thermostat fan to auto"
		send "Setting thermostat fan to auto"
		thermostat*.fanAuto()
        }
    }


def switchHandlerOn(evt) {
	updateStatus()
}

def switchHandlerOff(evt) {
	log.debug "Delaying setting thermostat fan to auto"
    runIn(delayOffMinutes * 60, updateStatus)
}

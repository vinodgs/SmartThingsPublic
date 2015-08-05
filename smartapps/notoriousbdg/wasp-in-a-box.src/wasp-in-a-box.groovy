def smartAppName() {
    return  "Wasp in a Box"
}

def smartAppVersion() {
    return  "Version 0.2.0"
}

def smartAppAuthor() {
    return  "Brandon Gordon"
}

def smartAppCopyright() {
    return  "Copyright (c) 2015 Brandon Gordon"
}

def smartAppNamespace() {
    return  "notoriousbdg"
}

def smartAppCategory() {
    return  "Convenience"
}

def smartAppSource() {
    return  "https://github.com/notoriousbdg/SmartThings.WiaB"
}

def smartAppDescription() {
    return  "Implements the 'Wasp in a Box' algorithm for occupancy.  Insipred by http://dreamgreenhouse.com/projects/2013/presence/index.php#wasp."
}

def smartAppRevision () {
    return  '2014-11-14  v0.1.0\n' +
            ' * Initial release\n' +
            '2015-03-08  v0.2.0\n' +
            ' * Support buttons\n' +
            ' * Support always on switches\n'
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
    name:        smartAppName(),
    namespace:   smartAppNamespace(),
    author:      smartAppAuthor(),
    description: smartAppDescription(),
    category:    smartAppCategory(),
    iconUrl:     "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:   "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
    page name:"pageStatus"
    page name:"pageAbout"
    page name:"pageSettings"
    page name:"pageEgress"
    page name:"pageInterior"
}

// Show About Page
def pageAbout() {
    def pageProperties = [
        name:           "pageAbout",
        title:          smartAppName(),
        nextPage:       "pageSettings",
        uninstall:      true
    ]

    state.version = smartAppVersion()

    return dynamicPage(pageProperties) {
        section() {
            paragraph smartAppDescription()
        }

        section() {
            paragraph smartAppVersion() + "\n" +
                      "Author " + smartAppAuthor() + "\n" +
                      smartAppCopyright()
        }

        section() {
            href      name: "sourceCode",
                      title: "Source Code (Tap to view)",
                      required: false,
                      external: true,
                      style: "external",
                      url: smartAppSource(),
                      description: smartAppSource()
        }

        section() {
            paragraph title: "Revision History",
                      smartAppRevision().trim()
        }
        
        section() {
            paragraph title: "License",
                      smartAppLicense()
        }  
    }
}

def pageStatus() {
    def pageProperties = [
        name:       "pageStatus",
        title:      smartAppName() + " Status",
        nextPage:   null,
        install:    true,
        uninstall:  true
    ]

    def helpPage = smartAppDescription()

    if (state.version != smartAppVersion()) {
        return pageAbout()
    }

    def listOccupancySensor = "Occupancy Sensor Name: ${settings.occupancySensorName}\nOccupancy Sensor Type: ${settings.occupancySensorType}"
    if (settings.decay) {
        listOccupancySensor += "\nOccupancy Decay: ${settings.decay}"
    }

    def listEgressSensors = ""

    if (settings.egressAcceleration) {
        listEgressSensors += "\nAcceleration Sensors Detect Motion\n"
        settings.egressAcceleration.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressButton) {
        listEgressSensors += "\nButtons Pressed\n"
        settings.egressButton.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressContactOpen) {
        listEgressSensors += "\nContact Sensors Open\n"
        settings.egressContactOpen.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressContactClose) {
        listEgressSensors += "\nContact Sensors Close\n"
        settings.egressContactClose.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressMotion) {
        listEgressSensors += "\nMotion Sensors Detect Motion\n"
        settings.egressMotion.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressPresence) {
        listEgressSensors += "\nPresence Sensors Depart\n"
        settings.egressPresence.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressSwitchOn) {
        listEgressSensors += "\nSwitches Turn On\n"
        settings.egressSwitchOn.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressSwitchOnPhysical) {
        listEgressSensors += "\nSwitches Physically Turn On\n"
        settings.egressSwitchOnPhysical.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.switchOn) {
        listEgressSensors += "\nSwitches Are Off\n"
        settings.switchOn.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressSwitchOff) {
        listEgressSensors += "\nSwitches Turn Off\n"
        settings.egressSwitchOff.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.egressSwitchOffPhysical) {
        listEgressSensors += "\nSwitches Physically Turn Off\n"
        settings.egressSwitchOffPhysical.each() {
            listEgressSensors += "  ${it.displayName}\n"
        }
    }

    def listInteriorSensors = ""

    if (settings.interiorAcceleration) {
        listInteriorSensors += "\nAcceleration Sensors Detect Motion\n"
        settings.interiorAcceleration.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorButton) {
        listInteriorSensors += "\nButtons Pressed\n"
        settings.interiorButton.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorContactOpen) {
        listInteriorSensors += "\nContact Sensors Open\n"
        settings.interiorContactOpen.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorContactClose) {
        listInteriorSensors += "\nContact Sensors Close\n"
        settings.interiorContactClose.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorMotion) {
        listInteriorSensors += "\nMotion Sensors Detect Motion\n"
        settings.interiorMotion.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorPresence) {
        listInteriorSensors += "\nPresence Sensors Arrive\n"
        settings.interiorPresence.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorSwitchOn) {
        listInteriorSensors += "\nSwitches Turn On\n"
        settings.interiorSwitchOn.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorSwitchOnPhysical) {
        listInteriorSensors += "\nSwitches Physically Turn On\n"
        settings.interiorSwitchOnPhysical.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.switchOn) {
        listInteriorSensors += "\nSwitches Are On\n"
        settings.switchOn.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorSwitchOff) {
        listInteriorSensors += "\nSwitches Turn Off\n"
        settings.interiorSwitchOff.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    if (settings.interiorSwitchOffPhysical) {
        listInteriorSensors += "\nSwitches Physically Turn Off\n"
        settings.interiorSwitchOffPhysical.each() {
            listInteriorSensors += "  ${it.displayName}\n"
        }
    }

    return dynamicPage(pageProperties) {
        section() {
            paragraph helpPage
            href "pageAbout", title:  "About", description: ""
        }

        section("Virtual Occupancy Sensor") {
            href "pageSettings", title: "Tap to Configure Virtual Occupancy Sensor", description: listOccupancySensor
        }

        section("Egress Sensors") {
            href "pageEgress", title: "Tap to Configure Egress Sensors", description: listEgressSensors.trim()
        }

        section("Interior Sensors") {
            href "pageInterior", title: "Tap to Configure Interior Sensors", description: listInteriorSensors.trim()
        }

        section ([title: "Settings", mobileOnly:true]) {
            label title: "Assign a name", defaultValue: settings.occupancySensorName, required: false
            mode title: "Set for specific mode(s)", required: false
        }
    }
}

def pageSettings() {
    def pageProperties = [
        name:       "pageSettings",
        title:      smartAppName() + " Settings",
        nextPage:   "pageStatus",
        uninstall:  true
    ]

    def helpPage = "Virtual occupancy sensor is the virtual sensor that will be used to reflect occupancy of a zone."

    def inputOccupancySensorName = [
        name:                  "occupancySensorName",
        type:                  "text",
        title:                 "Virtual occupancy sensor name?",
        required:              true
    ]

    def inputOccupancySensorType = [
        name:                  "occupancySensorType",
        type:                  "enum",
        title:                 "Virtual occupancy sensor type?",
        defaultValue:          "Presence Sensor",
        required:              true,
        options:               ["Presence Sensor", "Motion Detector", "Virtual Switch"]
    ]

    def inputDecay = [
        name:                  "decay",
        type:                  "integer",
        title:                 "Automatically set to unoccupied after how many minutes without interior activity? (Leave blank for never)",
        required:              false
    ]

    return dynamicPage(pageProperties) {
        section() {
            paragraph helpPage
        }

        section("Virtual Occupancy Sensor") {
            input inputOccupancySensorName
            input inputOccupancySensorType
        }

        section("Settings") {
            input inputDecay
        }
    }
}

def pageEgress() {
    def pageProperties = [
        name:       "pageEgress",
        title:      smartAppName() + " Egress Sensors",
        install:    false
    ]

    def helpPage = "Egress sensors are sensors that trip when someone leaves a zone."

    def inputEgressAcceleration = [
        name:                  "egressAcceleration",
        type:                  "capability.accelerationSensor",
        title:                 "When these acceleration sensors detect acceleration.",
        multiple:              true,
        required:              false
    ]

    def inputEgressButton = [
        name:                  "egressButton",
        type:                  "capability.button",
        title:                 "When these buttons are pressed.",
        multiple:              true,
        required:              false
    ]

    def inputEgressContactOpen = [
        name:                  "egressContactOpen",
        type:                  "capability.contactSensor",
        title:                 "When these contact sensors open.",
        multiple:              true,
        required:              false
    ]

    def inputEgressContactClose = [
        name:                  "egressContactClose",
        type:                  "capability.contactSensor",
        title:                 "When these contact sensors close.",
        multiple:              true,
        required:              false
    ]

    def inputEgressMotion = [
        name:                  "egressMotion",
        type:                  "capability.motionSensor",
        title:                 "When these motion sensors detect motion.",
        multiple:              true,
        required:              false
    ]

    def inputEgressPresence = [
        name:                  "egressPresence",
        type:                  "capability.presenceSensor",
        title:                 "When these presence sensors depart.",
        multiple:              true,
        required:              false
    ]

    def inputEgressSwitchOn = [
        name:                  "egressSwitchOn",
        type:                  "capability.switch",
        title:                 "When these switches turn on.",
        multiple:              true,
        required:              false
    ]

    def inputEgressSwitchOnPhysical = [
        name:                  "egressSwitchOnPhysical",
        type:                  "capability.switch",
        title:                 "When these switches turn on with a physical button press.",
        multiple:              true,
        required:              false
    ]

    def inputEgressSwitchOffAlways = [
        name:                  "switchOn",
        type:                  "capability.switch",
        title:                 "When these switches are off.",
        multiple:              true,
        required:              false
    ]

    def inputEgressSwitchOff = [
        name:                  "egressSwitchOff",
        type:                  "capability.switch",
        title:                 "When these switches turn off.",
        multiple:              true,
        required:              false
    ]

    def inputEgressSwitchOffPhysical = [
        name:                  "egressSwitchOffPhysical",
        type:                  "capability.switch",
        title:                 "When these switches turn off with a physical button press.",
        multiple:              true,
        required:              false
    ]

    return dynamicPage(pageProperties) {
        section() {
            paragraph helpPage
        }

        section("Egress Sensors") {
            input inputEgressAcceleration
            input inputEgressButton
            input inputEgressContactOpen
            input inputEgressContactClose
            input inputEgressMotion
            input inputEgressPresence
            input inputEgressSwitchOn
            input inputEgressSwitchOnPhysical
            input inputEgressSwitchOffAlways
            input inputEgressSwitchOff
            input inputEgressSwitchOffPhysical
        }
    }
}

def pageInterior() {
    def pageProperties = [
        name:       "pageInterior",
        title:      smartAppName() + " Interior Sensors",
        install:    false
    ]

    def helpPage = "Interior sensors are sensors that trip when a zone is occupied."

    def inputInteriorAcceleration = [
        name:                  "interiorAcceleration",
        type:                  "capability.accelerationSensor",
        title:                 "When these acceleration sensors detect acceleration.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorButton = [
        name:                  "interiorButton",
        type:                  "capability.button",
        title:                 "When these buttons are pressed.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorContactOpen = [
        name:                  "interiorContactOpen",
        type:                  "capability.contactSensor",
        title:                 "When these contact sensors open.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorContactClose = [
        name:                  "interiorContactClose",
        type:                  "capability.contactSensor",
        title:                 "When these contact sensors close.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorMotion = [
        name:                  "interiorMotion",
        type:                  "capability.motionSensor",
        title:                 "When these motion sensors detect motion.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorPresence = [
        name:                  "interiorPresence",
        type:                  "capability.presenceSensor",
        title:                 "When these presence sensors arrive.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorSwitchOn = [
        name:                  "interiorSwitchOn",
        type:                  "capability.switch",
        title:                 "When these switches turn on.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorSwitchOnPhysical = [
        name:                  "interiorSwitchOnPhysical",
        type:                  "capability.switch",
        title:                 "When these switches turn on with a physical button press.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorSwitchOnAlways = [
        name:                  "switchOn",
        type:                  "capability.switch",
        title:                 "When these switches are on.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorSwitchOff = [
        name:                  "interiorSwitchOff",
        type:                  "capability.switch",
        title:                 "When these switches turn off.",
        multiple:              true,
        required:              false
    ]

    def inputInteriorSwitchOffPhysical = [
        name:                  "interiorSwitchOffPhysical",
        type:                  "capability.switch",
        title:                 "When these switches turn off with a physical button press.",
        multiple:              true,
        required:              false
    ]

    return dynamicPage(pageProperties) {
        section() {
            paragraph helpPage
        }

        section("Interior Sensors") {
            input inputInteriorAcceleration
            input inputInteriorButton
            input inputInteriorContactOpen
            input inputInteriorContactClose
            input inputInteriorMotion
            input inputInteriorPresence
            input inputInteriorSwitchOn
            input inputInteriorSwitchOnPhysical
            input inputInteriorSwitchOnAlways
            input inputInteriorSwitchOff
            input inputInteriorSwitchOffPhysical
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

    // Egress Sensors
    subscribe(egressAcceleration,         "acceleration.active",  egressHandler)
    subscribe(egressButton,               "button",               egressHandler)
    subscribe(egressContactOpen,          "contact.open",         egressHandler)
    subscribe(egressContactClose,         "contact.closed",       egressHandler)
    subscribe(egressMotion,               "motion.active",        egressHandler)
    subscribe(egressPresence,             "presence.not present", egressHandler)
    subscribe(egressSwitchOn,             "switch.on",            egressHandler)
    subscribe(egressSwitchOff,            "switch.off",           egressHandler)
    subscribe(switchOn,                   "switch.off",           egressHandler)
    subscribe(egressSwitchOnPhysical,     "switch.on",            egressHandlerPhysical)
    subscribe(egressSwitchOffPhysical,    "switch.off",           egressHandlerPhysical)
    // Interior Sensors
    subscribe(interiorAcceleration,       "acceleration.active",  interiorHandler)
    subscribe(interiorButton,             "button",               interiorHandler)
    subscribe(interiorContactOpen,        "contact.open",         interiorHandler)
    subscribe(interiorContactClose,       "contact.closed",       interiorHandler)
    subscribe(interiorMotion,             "motion.active",        interiorHandler)
    subscribe(interiorPresence,           "presence.present",     interiorHandler)
    subscribe(interiorSwitchOn,           "switch.on",            interiorHandler)
    subscribe(interiorSwitchOff,          "switch.off",           interiorHandler)
    subscribe(switchOn,                   "switch.on",            interiorHandler)
    subscribe(interiorSwitchOnPhysical,   "switch.on",            interiorHandlerPhysical)
    subscribe(interiorSwitchOffPhysical,  "switch.off",           interiorHandlerPhysical)

    if (settings.childId == null) {
        settings.childId = "Occupancy." + now()
    }

    try {
        def existingDevice = getChildDevices()
        if(existingDevice) {
            log.debug "Child already exists"
            if (state.occupancySensorType == settings.occupancySensorType) {
                log.debug "Child device type has not changed"
            } else {
                log.debug "Child device type changed, deleting child"
                existingDevice.each {
                    deleteChildDevice(it.deviceNetworkId)
                }
            }
        }

        existingDevice = getChildDevices()
        if(!existingDevice) {
            log.debug "Creating child"
            state.occupancySensorType = settings.occupancySensorType
            switch (settings.occupancySensorType) {
                case "Presence Sensor":
                    def childDevice = addChildDevice("smartthings", "Mobile Presence", settings.childId, null, [name: "WiaB Occupancy Sensor", label: settings.occupancySensorName, completedSetup: true])
                    break;
                case "Motion Detector":
                    def childDevice = addChildDevice("smartthings", "Motion Detector", settings.childId, null, [name: "WiaB Occupancy Sensor", label: settings.occupancySensorName, completedSetup: true])
                    break;
                case "Virtual Switch":
                    def childDevice = addChildDevice("smartthings", "On/Off Button Tile", settings.childId, null, [name: "WiaB Occupancy Sensor", label: settings.occupancySensorName, completedSetup: true])
                    break;
            }
        }
    } catch (e) {
        log.error "Error creating device: ${e}"
    }

    runIn(60, egress)
}

def interiorHandler(evt) {
    log.debug "interiorHandler: $evt.displayName $evt.name $evt.value"
    interior()
}

def interiorHandlerPhysical(evt) {
    if (evt.isPhysical()) {
        log.debug "interiorHandlerSwitch: $evt.displayName $evt.name $evt.value"
        interior()
    }
}

def interior() {
    log.debug "interior"

    def children = getChildDevices()
    children.each {
        log.info  "Setting $it.label to occupied"
        switch (settings.occupancySensorType) {
            case "Presence Sensor":
                it.sendEvent(name: 'presence', value: "present")
                break;
            case "Motion Detector":
                it.sendEvent(name: 'motion', value: "active")
                break;
            case "Virtual Switch":
                it.on()
                break;
        }
    }

    if (settings.decay != null && settings.decay > 0) {
        log.info  "Schduling egress to occur in $settings.decay minutes"
        unschedule()
        runIn(settings.decay.toInteger() * 60, egress)
    }
}

def egressHandler(evt) {
    log.debug "egressHandler: $evt.displayName $evt.name $evt.value"
    egress()
}

def egressHandlerPhysical(evt) {
    if (evt.isPhysical()) {
        log.debug "egressHandlerSwitch: $evt.displayName $evt.name $evt.value"
        egress()
    }
}

def egress() {
    log.debug "egress"

    def turnOff = true
    
    settings.switchOn.each() {
        if (it.currentValue("switch") == "on") {
            turnOff = false
        }
    }

    if (turnOff) {
    def children = getChildDevices()
    children.each {
        log.info  "Setting $it.label to not occupied"
        switch (settings.occupancySensorType) {
            case "Presence Sensor":
                it.sendEvent(name: 'presence', value: "not present")
                break;
            case "Motion Detector":
                it.sendEvent(name: 'motion', value: "inactive")
                break;
            case "Virtual Switch":
                it.off()
                break;
        }
    }
    }
}

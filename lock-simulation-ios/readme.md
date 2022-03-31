# Lock Simulator

Lock simulator is built to imitate the behaviour of a electronically-controlled lock. For an actual lock, a NFC receiver and a mechanical actuator is needed to receive authentication signal and perform unlocking physically. To simulate the process of authentication, only a NFC receiver is required and the result of said process can be shown on a screen.

iOS is chosen as the platform of the simulator.

## Requirments

- Xcode 13.3.1
- a physical iOS device (iPhone 8 or later)
- an Apple Developer Account with an active membership (Free account will not work because of the usage of CoreNFC)

## Build Instruction

1. Clone this repository, open `LockSimulator.xcodeproj` with Xcode
2. In the project navigator, select the project root > target LockSimulator > Signing & Capabilities > Signing, choose a developer team with an active membership
3. Select a device to build to, and hit run
4. The app should be run on your device

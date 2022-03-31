//
//  SetupPairUpView.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 6/5/2022.
//

import SwiftUI

struct SetupPairUpView: View {
    @AppStorage("privateKey") var privateKey = ""
    @State var shouldNavigateToNextScreen = false
    @ObservedObject var tagReader = TagReaderController()

    var body: some View {
        VStack () {
                        
            IconLock()
                .fill(.blue)
                .frame(width: 44, height: 50)
            
            
            Text("Follow the steps below")
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding(.top, 72)
            
            Spacer().frame(height: 36)
            
            Text("1. In Home Screen, under lock section, choose Add Door\n\n2. Fill out the door information, and click on \"Tap to save NFC signal\"\n\n3. The ShareLock App will start transmitting a pair-up code. Click the button below to receive it")
                .padding()
            
            Spacer()
            
            Button("Receive the Pair-Up Code") {
                tagReader.scanAlertMsg = "Receiving pair-up code..."
                tagReader.beginScanning()
            }
            .buttonStyle(BlueFullWidthButton())
            .onChange(of: tagReader.scannedData) { newData in
                if (newData != nil && newData!.isEmpty) {
                    tagReader.beginScanning()
                    return
                }
                
                privateKey = newData!
                shouldNavigateToNextScreen = true
            }
            
            NavigationLink(destination: SetupFinishView().navigationBarBackButtonHidden(true), isActive: $shouldNavigateToNextScreen) { EmptyView() }
            
        }
        .padding()
        .interactiveDismissDisabled(true)
    }
}

struct SetupPairUpView_Previews: PreviewProvider {
    static var previews: some View {
        SetupPairUpView()
    }
}

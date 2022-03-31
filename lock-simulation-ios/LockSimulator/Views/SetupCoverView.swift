//
//  SetupCoverView.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 6/5/2022.
//

import SwiftUI

struct SetupCoverView: View {

    var body: some View {
        VStack () {
            
            IconLock()
                .fill(.blue)
                .frame(width: 44, height: 50)
            
            
            Text("Set Up Your New Lock")
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding(.top, 72)
            
            Spacer().frame(height: 36)
            
            Text("You will be setting up as the owner of this lock.")
            
            Spacer().frame(height: 16)
            
            Text("Prepare your ShareLock App.")
            Text("When you are ready, click Continue.")
            
            Spacer()
            
            NavigationLink(destination: SetupPairUpView().navigationBarBackButtonHidden(true)) {
                Text("Continue")
            }
            .buttonStyle(BlueFullWidthButton())
            
        }
        .padding()
        .interactiveDismissDisabled(true)
    }
}
struct SetupCoverView_Previews: PreviewProvider {
    static var previews: some View {
        SetupCoverView()
    }
}

//
//  SetupFinishView.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 6/5/2022.
//

import SwiftUI

struct SetupFinishView: View {
    @Environment(\.isShowingSheet) var isShowingSheet

    var body: some View {
        VStack () {
                        
            IconCheck()
                .fill(.green)
                .frame(width: 44, height: 50)
            
            
            Text("Your Lock is Ready")
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding(.top, 72)
            
            Spacer()
            
            Button("Continue") {
                self.isShowingSheet.wrappedValue = false
            }
            .buttonStyle(BlueFullWidthButton())
            
        }
        .padding()
        .interactiveDismissDisabled(true)
    }
}

struct SetupFinishView_Previews: PreviewProvider {
    static var previews: some View {
        SetupFinishView()
    }
}

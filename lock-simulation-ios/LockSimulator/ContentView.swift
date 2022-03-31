//
//  ContentView.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 6/5/2022.
//

import SwiftUI

struct ContentView: View {
//    State variable to bind to the EnvironmentalValue - isShowingSheet
    @AppStorage("privateKey") var privateKey = ""
    @State private var isShowingSheetLocal = false;
    
    var body: some View {
 
        LockView()
            .environment(\.isShowingSheet, self.$isShowingSheetLocal)
            .onAppear {
                self.isShowingSheetLocal = self.privateKey.isEmpty ? true : false
            }
            .sheet(isPresented: $isShowingSheetLocal) {
                    SetupWrapperView()
                    .environment(\.isShowingSheet, self.$isShowingSheetLocal)
            }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}



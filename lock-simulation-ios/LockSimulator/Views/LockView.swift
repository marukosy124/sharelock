//
//  LockView.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 7/5/2022.
//

import SwiftUI

struct getLockIcon: View {
    var isLocked: Bool
    
    init(_ isLocked: Bool) {
        self.isLocked = isLocked
    }
    
    var body: some View {
        if isLocked {
            IconLock()
                .fill(.red)
                .frame(width: 44, height: 50)
        } else {
            IconLockOpen()
                .fill(.green)
                .frame(width: 56, height: 50)
        }
    }
}

struct LockView: View {
    @AppStorage("privateKey") var privateKey = ""
    @Environment(\.isShowingSheet) var isShowingSheet
    @State var isLocked = true
    @State var titleText = "Locked"
    @State var actionText = "Start Scanning"
    
    @ObservedObject var tagReader = TagReaderController()
    
    func resetLock() {
        self.privateKey = ""
        self.isShowingSheet.wrappedValue = true
    }
    
    func updateTextsByLockState() {
        updateTitleTextByLockState()
        updateActionTextByLockState()
    }
    
    func updateActionTextByLockState() {
        actionText = isLocked ? "Start Scanning" : "Lock"
    }
    
    func updateTitleTextByLockState() {
        titleText = isLocked ? "Locked" : "Unlocked"
    }

    var body: some View {
        NavigationView {
            VStack {
                
                getLockIcon(self.isLocked)
                
                Text(self.titleText)
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .padding(.top, 72)
                
                Spacer()
                
                Button(self.actionText) {
                    if self.isLocked {
                        tagReader.beginScanning()
                    } else {
                        self.isLocked.toggle()
                        updateTextsByLockState()
                    }
                }
                .buttonStyle(BlueFullWidthButton())
                .onChange(of: tagReader.scannedData) { newData in
                    if newData == nil {
                        return
                    }
                    
                    let isValid = isTokenValid(token: newData!)
                    
                    if isValid == true {
                        self.isLocked.toggle()
                        updateTextsByLockState()
                    } else {
                        titleText = "Unlock Failed"
                        tagReader.beginScanning()
                    }
                }
                .onChange(of: tagReader.isSystemResourceUnavailable) { isUnavailable in
                    if isUnavailable {
                        actionText = "Loading..."
                    } else {
                        updateActionTextByLockState()
                    }
                }
            }
            .padding()
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing, content: {
                    Menu(content: {
                        Button(role: .destructive, action: resetLock, label: {
                            Label("Reset", systemImage: "trash")
                        })
                    }, label: {
                        Image(systemName: "ellipsis.circle")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 24, height: 24)
                    })
                })
            }
        }
        .onAppear() {
            if !privateKey.isEmpty {
                tagReader.beginScanning()
            }            
        }
    }
}

struct LockView_Previews: PreviewProvider {
    static var previews: some View {
        LockView()
    }
}

//
//  TagReaderController.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 7/5/2022.
//

import Foundation
import CoreNFC

class TagReaderController: NSObject, ObservableObject, NFCNDEFReaderSessionDelegate {
    
    private var session: NFCNDEFReaderSession?
    var scanAlertMsg = "Put ShareLock App into unlock mode and hold near me."
    
    @Published var scannedData: String?
    @Published var isSystemResourceUnavailable: Bool = false
    
    func beginScanning() {
        guard NFCNDEFReaderSession.readingAvailable else {
            print("This device is unsupported")
            return
        }
        
        session = NFCNDEFReaderSession(delegate: self, queue: DispatchQueue.main, invalidateAfterFirstRead: false)
        session?.alertMessage = scanAlertMsg
        session?.begin()
        
        isSystemResourceUnavailable = false
        scannedData = nil
    }
    
    func readerSessionDidBecomeActive(_ session: NFCNDEFReaderSession) {
        
    }
    
    func readerSession(_ session: NFCNDEFReaderSession, didInvalidateWithError error: Error) {
        if let readerError = error as? NFCReaderError {
            if readerError.code == .readerSessionInvalidationErrorSystemIsBusy {
                DispatchQueue.main.async {
                    self.isSystemResourceUnavailable = true
                }
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                    self.beginScanning() // retry
                }
                
                return
            }
            
            if readerError.code == .readerSessionInvalidationErrorUserCanceled {
                return
            }
            
            if readerError.code == .readerSessionInvalidationErrorFirstNDEFTagRead {
                return
            }
        }
        
        // print unhandled errors
        print("TagReaderController \(error.localizedDescription)")
    }
    
    func readerSession(_ session: NFCNDEFReaderSession, didDetectNDEFs messages: [NFCNDEFMessage]) {
        for message in messages {
            for record in message.records {
                var payload = String(data: record.payload, encoding: .utf8) ?? "   Invalid Data"
                payload = trimFirst3Bytes(input: payload)
                
                scannedData = payload
            }
        }
        session.invalidate()
    }
    
    func trimFirst3Bytes(input: String) -> String {
        let index = input.index(input.startIndex, offsetBy: 3)
        return String(input[index...])
    }

    override init() {
        super.init()
    }
}

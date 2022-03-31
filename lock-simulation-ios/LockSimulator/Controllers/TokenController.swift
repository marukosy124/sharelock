//
//  TokenController.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 7/5/2022.
//

import SwiftUI
import JWT

func isTokenValid(token: String) -> Bool {
    @AppStorage("privateKey") var privateKey = ""
    
    do {
        let _: ClaimSet = try JWT.decode(
            token,
            algorithm: .hs256(privateKey.data(using: .utf8)!),
            leeway: 0
        )
        
        // if things goes right, return true
        return true
        
    } catch {
        print("isTokenValid token not parsable")
        return false
    }
}

//
//  BlueFullWidthButton.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 6/5/2022.
//

import SwiftUI

struct BlueFullWidthButton: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding()
            .frame(minWidth: 0, maxWidth: .infinity)
            .background(.blue)
            .opacity(configuration.isPressed ? 0.7 : 1)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: 10))
            .controlSize(.large)
    }
}

struct BlueFullWidthButton_Previews: PreviewProvider {
    static var previews: some View {
        Button("Continue") {
            
        }
        .buttonStyle(BlueFullWidthButton())
    }
}

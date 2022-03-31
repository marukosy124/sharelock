//
//  SetupWrapperView.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 7/5/2022.
//

import SwiftUI

struct ShowingSheetKey: EnvironmentKey {
    static let defaultValue: Binding<Bool> = .constant(false)
}

extension EnvironmentValues {
    var isShowingSheet: Binding<Bool>  {
        get { self[ShowingSheetKey.self] }
        set { self[ShowingSheetKey.self] = newValue}
    }
}

struct SetupWrapperView: View {
    var body: some View {
        NavigationView {
            SetupCoverView()
        }
    }
}

struct SetupWrapperView_Previews: PreviewProvider {
    static var previews: some View {
        SetupWrapperView()
    }
}

//
//  IconLockOpen.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 7/5/2022.
//

import SwiftUI

struct IconLockOpen: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let width = rect.size.width
        let height = rect.size.height
        path.move(to: CGPoint(x: 0.61111*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.66667*width, y: 0.375*height))
        path.addCurve(to: CGPoint(x: 0.77778*width, y: 0.5*height), control1: CGPoint(x: 0.72795*width, y: 0.375*height), control2: CGPoint(x: 0.77778*width, y: 0.43105*height))
        path.addLine(to: CGPoint(x: 0.77778*width, y: 0.875*height))
        path.addCurve(to: CGPoint(x: 0.66667*width, y: height), control1: CGPoint(x: 0.77778*width, y: 0.94395*height), control2: CGPoint(x: 0.72795*width, y: height))
        path.addLine(to: CGPoint(x: 0.11111*width, y: height))
        path.addCurve(to: CGPoint(x: 0, y: 0.875*height), control1: CGPoint(x: 0.04974*width, y: height), control2: CGPoint(x: 0, y: 0.94395*height))
        path.addLine(to: CGPoint(x: 0, y: 0.5*height))
        path.addCurve(to: CGPoint(x: 0.11111*width, y: 0.375*height), control1: CGPoint(x: 0, y: 0.43105*height), control2: CGPoint(x: 0.04974*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.5*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.5*width, y: 0.28125*height))
        path.addCurve(to: CGPoint(x: 0.75*width, y: 0), control1: CGPoint(x: 0.5*width, y: 0.12592*height), control2: CGPoint(x: 0.61198*width, y: 0))
        path.addCurve(to: CGPoint(x: width, y: 0.28125*height), control1: CGPoint(x: 0.88802*width, y: 0), control2: CGPoint(x: width, y: 0.12592*height))
        path.addLine(to: CGPoint(x: width, y: 0.375*height))
        path.addCurve(to: CGPoint(x: 0.94444*width, y: 0.4375*height), control1: CGPoint(x: width, y: 0.40957*height), control2: CGPoint(x: 0.97517*width, y: 0.4375*height))
        path.addCurve(to: CGPoint(x: 0.88889*width, y: 0.375*height), control1: CGPoint(x: 0.91372*width, y: 0.4375*height), control2: CGPoint(x: 0.88889*width, y: 0.40957*height))
        path.addLine(to: CGPoint(x: 0.88889*width, y: 0.28125*height))
        path.addCurve(to: CGPoint(x: 0.75*width, y: 0.125*height), control1: CGPoint(x: 0.88889*width, y: 0.19496*height), control2: CGPoint(x: 0.82674*width, y: 0.125*height))
        path.addCurve(to: CGPoint(x: 0.61111*width, y: 0.28125*height), control1: CGPoint(x: 0.67326*width, y: 0.125*height), control2: CGPoint(x: 0.61111*width, y: 0.19496*height))
        path.addLine(to: CGPoint(x: 0.61111*width, y: 0.375*height))
        path.closeSubpath()
        return path
    }
}

struct IconLockOpen_Previews: PreviewProvider {
    static var previews: some View {
        IconLockOpen().fill(.green)
            .previewLayout(.fixed(width: 56, height: 50))
    }
}

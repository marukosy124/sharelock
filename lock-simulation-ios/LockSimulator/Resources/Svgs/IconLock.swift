//
//  IconLock.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 6/5/2022.
//

import SwiftUI

struct IconLock: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let width = rect.size.width
        let height = rect.size.height
        path.move(to: CGPoint(x: 0.17857*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.17857*width, y: 0.28125*height))
        path.addCurve(to: CGPoint(x: 0.5*width, y: 0), control1: CGPoint(x: 0.17857*width, y: 0.12592*height), control2: CGPoint(x: 0.32254*width, y: 0))
        path.addCurve(to: CGPoint(x: 0.82143*width, y: 0.28125*height), control1: CGPoint(x: 0.67746*width, y: 0), control2: CGPoint(x: 0.82143*width, y: 0.12592*height))
        path.addLine(to: CGPoint(x: 0.82143*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.85714*width, y: 0.375*height))
        path.addCurve(to: CGPoint(x: width, y: 0.5*height), control1: CGPoint(x: 0.93594*width, y: 0.375*height), control2: CGPoint(x: width, y: 0.43105*height))
        path.addLine(to: CGPoint(x: width, y: 0.875*height))
        path.addCurve(to: CGPoint(x: 0.85714*width, y: height), control1: CGPoint(x: width, y: 0.94395*height), control2: CGPoint(x: 0.93594*width, y: height))
        path.addLine(to: CGPoint(x: 0.14286*width, y: height))
        path.addCurve(to: CGPoint(x: 0, y: 0.875*height), control1: CGPoint(x: 0.06395*width, y: height), control2: CGPoint(x: 0, y: 0.94395*height))
        path.addLine(to: CGPoint(x: 0, y: 0.5*height))
        path.addCurve(to: CGPoint(x: 0.14286*width, y: 0.375*height), control1: CGPoint(x: 0, y: 0.43105*height), control2: CGPoint(x: 0.06395*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.17857*width, y: 0.375*height))
        path.closeSubpath()
        path.move(to: CGPoint(x: 0.32143*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.67857*width, y: 0.375*height))
        path.addLine(to: CGPoint(x: 0.67857*width, y: 0.28125*height))
        path.addCurve(to: CGPoint(x: 0.5*width, y: 0.125*height), control1: CGPoint(x: 0.67857*width, y: 0.19496*height), control2: CGPoint(x: 0.59866*width, y: 0.125*height))
        path.addCurve(to: CGPoint(x: 0.32143*width, y: 0.28125*height), control1: CGPoint(x: 0.40134*width, y: 0.125*height), control2: CGPoint(x: 0.32143*width, y: 0.19496*height))
        path.addLine(to: CGPoint(x: 0.32143*width, y: 0.375*height))
        path.closeSubpath()
        return path
    }
}

struct IconLock_Previews: PreviewProvider {
    static var previews: some View {
        IconLock()
            .fill(.blue)
            .previewLayout(.fixed(width: 44, height: 50))
    }
}

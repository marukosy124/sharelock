//
//  IconCheck.swift
//  LockSimulator
//
//  Created by Chon Hou Leong on 7/5/2022.
//

import SwiftUI

struct IconCheck: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let width = rect.size.width
        let height = rect.size.height
        path.move(to: CGPoint(x: 0.97902*width, y: 0.20586*height))
        path.addCurve(to: CGPoint(x: 0.97902*width, y: 0.29414*height), control1: CGPoint(x: 1.00692*width, y: 0.23027*height), control2: CGPoint(x: 1.00692*width, y: 0.26973*height))
        path.addLine(to: CGPoint(x: 0.40759*width, y: 0.79414*height))
        path.addCurve(to: CGPoint(x: 0.3067*width, y: 0.79414*height), control1: CGPoint(x: 0.37969*width, y: 0.81855*height), control2: CGPoint(x: 0.3346*width, y: 0.81855*height))
        path.addLine(to: CGPoint(x: 0.02092*width, y: 0.54414*height))
        path.addCurve(to: CGPoint(x: 0.02092*width, y: 0.45586*height), control1: CGPoint(x: -0.00697*width, y: 0.51973*height), control2: CGPoint(x: -0.00697*width, y: 0.48027*height))
        path.addCurve(to: CGPoint(x: 0.12194*width, y: 0.45586*height), control1: CGPoint(x: 0.04882*width, y: 0.43145*height), control2: CGPoint(x: 0.09404*width, y: 0.43145*height))
        path.addLine(to: CGPoint(x: 0.35513*width, y: 0.66152*height))
        path.addLine(to: CGPoint(x: 0.87812*width, y: 0.20586*height))
        path.addCurve(to: CGPoint(x: 0.97902*width, y: 0.20586*height), control1: CGPoint(x: 0.90603*width, y: 0.18141*height), control2: CGPoint(x: 0.95112*width, y: 0.18141*height))
        path.addLine(to: CGPoint(x: 0.97902*width, y: 0.20586*height))
        path.closeSubpath()
        return path
    }
}

struct IconCheck_Previews: PreviewProvider {
    static var previews: some View {
        IconCheck().fill(.green)
            .previewLayout(.fixed(width: 44, height: 50))
    }
}

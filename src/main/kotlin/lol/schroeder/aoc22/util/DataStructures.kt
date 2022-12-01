package lol.schroeder.aoc22.util

data class Point(val x: Int, val y: Int)
data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
    constructor(point1: Point, point2: Point): this(point1.x, point1.y, point2.x, point2.y)
}

data class Vector(val x: Int, val y: Int, val z: Int)
data class Point3D(val x: Int, val y: Int, val z: Int)

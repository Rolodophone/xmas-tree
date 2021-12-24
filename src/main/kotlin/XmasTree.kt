import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.extensions.SingleScreenshot
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

const val SCREENSHOT_AND_EXIT = true

const val SEED = 536

const val LEAF_SPACING = 0.5
const val LEAF_MAX_ANGLE = PI/4.0
const val LEAF_HUE = 80.0
const val LEAF_SATURATION = 0.7
const val LEAF_MIN_VALUE = 0.1
const val LEAF_MAX_VALUE = 0.2
const val LEAF_VALUE_RANGE = 0.1
const val LEAF_LENGTH = 15.0

const val BRANCH_X_SPACING = 15
const val BRANCH_Y_SPACING = 15
const val BRANCH_LENGTH = 50.0

const val TREE_X = 400
const val TREE_Y = 700
const val TREE_RADIUS = 200
const val TREE_HEIGHT = 600

const val TRUNK_WIDTH = 4.0
const val TRUNK_COLOUR = 0x4E3C2E

fun main() = application {
	configure {
		width = 800
		height = 800
	}
	program {
		var rand = Random(SEED)

		fun drawBranch(drawer: Drawer, x: Double, y: Double, angleRad: Double, length: Double,
					   minValue: Double, maxValue: Double) {
			var leafX = x
			var leafY = y
			var leafAngle = angleRad + rand.nextDouble(-LEAF_MAX_ANGLE, LEAF_MAX_ANGLE)
			var leafColour = ColorHSVa(LEAF_HUE, LEAF_SATURATION, rand.nextDouble(minValue, maxValue))

			while (true) {
				drawer.stroke = leafColour.toRGBa()
				drawer.lineSegment(
					leafX,
					leafY,
					leafX + LEAF_LENGTH * cos(leafAngle),
					leafY - LEAF_LENGTH * sin(leafAngle)
				)

				leafX += LEAF_SPACING * cos(angleRad)
				leafY -= LEAF_SPACING * sin(angleRad)
				leafAngle = angleRad + rand.nextDouble(-LEAF_MAX_ANGLE, LEAF_MAX_ANGLE)
				leafColour = ColorHSVa(LEAF_HUE, LEAF_SATURATION, rand.nextDouble(minValue, maxValue))

				//stop drawing leaves when reached end of branch
				if (angleRad % (2 * PI) < PI) { // acute/obtuse therefore y decreasing
					if (leafY < y - length * sin(angleRad)) break
				}
				else { // reflex therefore y increasing
					if (leafY > y - length * sin(angleRad)) break
				}
			}
		}

		fun drawTree(drawer: Drawer, radius: Int, height: Int) {
			//trunk
			drawer.strokeWeight = TRUNK_WIDTH
			drawer.stroke = ColorRGBa.fromHex(TRUNK_COLOUR)
			drawer.lineSegment(TREE_X.toDouble(), TREE_Y.toDouble(), TREE_X.toDouble(), (TREE_Y - height).toDouble())

			//branches
			drawer.strokeWeight = 1.0
			for (branchY in (TREE_Y - height)..TREE_Y step BRANCH_Y_SPACING) {
				val currentRadius = (radius * (branchY - TREE_Y + height)) / height

				for (branchX in (TREE_X - currentRadius)..(TREE_X + currentRadius) step BRANCH_X_SPACING) {
					val minValue = rand.nextDouble(LEAF_MIN_VALUE, LEAF_MAX_VALUE)
					drawBranch(
						drawer,
						branchX.toDouble(),
						branchY.toDouble(),
						rand.nextDouble(PI, 2 * PI),
						BRANCH_LENGTH,
						minValue,
						minValue + LEAF_VALUE_RANGE
					)
				}
			}
		}

		if (SCREENSHOT_AND_EXIT) extend(SingleScreenshot())

		extend {
			drawer.clear(ColorRGBa.WHITE)
			drawTree(drawer, TREE_RADIUS, TREE_HEIGHT)
			rand = Random(SEED)
		}
	}
}
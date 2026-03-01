import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

// Import the Vehicle class from its actual package if available
// import your.package.name.Vehicle

class VehicleTest {
    @Test
    fun testEngineStart() {
        // Arrange
        val vehicle = Vehicle("Car")
        val engine = vehicle.Engine(150)
        // Act
        engine.start()
        // Assert
        // (Add assertions if engine.start() has observable effects)
    }

    @Test
    fun testEnginePrintHorsePower() {
        // Arrange
        val vehicle = Vehicle("Motorcycle")
        val engine = vehicle.Engine(80)
        // Act
        engine.printHorsePower()
        // Assert
        assertEquals(80, engine.horsePower)
    }

    @Test
    fun testVehicleNameAccess() {
        // Arrange
        val vehicle = Vehicle("Truck")
        val engine = vehicle.Engine(300)
        // Act
        // (No action needed, just access)
        // Assert
        assertEquals("Truck", vehicle.name)
    }
}


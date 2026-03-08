import org.hyperskill.hstest.stage.SpringTest
import org.hyperskill.hstest.testcase.CheckResult
import org.hyperskill.hstest.testing.expect.Expectation.expect
import org.hyperskill.hstest.testing.expect.json.JsonChecker.isArray
import org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject

class CinemaTests : SpringTest() {
    private val totalRows = 9
    private val totalCols = 9

    fun testEndpoint(): CheckResult {
        val response = get("/seats").send()

        if (response.statusCode != 200) {
            return CheckResult.wrong(
                "GET /seats should respond with " +
                        "status code 200, responded: " + response.statusCode + "\n\n" +
                        "Response body:\n" + response.content
            )
        }

        return CheckResult.correct()
    }

    fun testEndpointAvailableSeats(): CheckResult {
        val response = get("/seats").send()

        var arrayBuilder = isArray(totalRows * totalCols)
        for (i in 1..totalRows) {
            for (j in 1..totalCols) {
                val objectBuilder = isObject()
                    .value("row", i)
                    .value("column", j)
                arrayBuilder = arrayBuilder.item(objectBuilder)
            }
        }
        expect(response.content).asJson().check(
            isObject()
                .value("available_seats", arrayBuilder)
                .value("total_columns", totalCols)
                .value("total_rows", totalRows)
        )

        return CheckResult.correct()
    }
}

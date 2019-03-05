package uk.gov.hmrc.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class MRNLoadRunner extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8268")

  val scn = scenario("Process MRN")
    .group("Holyhead") {
      exec(http("request_ports")
        .get("/common-transit-convention/transits/Holyhead/backlog")
          .check(regex("""<input type="hidden" name="csrfToken" value="(.*)"/>""").saveAs("CSRF"))
        .resources(http("request_holyhead")
          .post("/common-transit-convention/transits")
          .formParam("completed", "Yes")
          .formParam("destinationPort", "Holyhead")
          .formParam("csrfToken", "${CSRF}")
          .formParam("notes", "")
          .formParam("transitId", "5c726c7b2b0000840127c76d")
          .formParam("transitState", "backlog")))
        .pause(15)
    }
  setUp(scn.inject(
    nothingFor(4 seconds),
    atOnceUsers(10),
    rampUsers(10) during (5 seconds),
    constantUsersPerSec(20) during (15 seconds),
//    constantUsersPerSec(20) during (15 seconds) randomized,
//    rampUsersPerSec(10) to 20 during (10 minutes),
//    rampUsersPerSec(10) to 20 during (10 minutes) randomized,
//    heavisideUsers(1000) during (20 seconds)
  ).protocols(httpProtocol))
}

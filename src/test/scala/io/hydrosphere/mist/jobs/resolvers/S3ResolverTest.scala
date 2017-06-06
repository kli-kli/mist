package io.hydrosphere.mist.jobs.resolvers

import java.nio.file.{Files, Paths}

import io.hydrosphere.mist.jobs.JobFile
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

class S3ResolverTest extends FunSuite with Matchers {

  val existUrl = "https://nutrition001.s3.amazonaws.com/006.html"
  val nonExistUrl = "https://someBucket123.s3.amazonaws.com/someKey123"
  val incorectUrl =  "http://abracadabra.com/"

  test("should throw exception if incorrect url") {
    an [IllegalArgumentException] should be thrownBy new S3Resolver(incorectUrl)
  }

  test("exist should return false if this object doesnt exist") {
    val resolver = new S3Resolver(nonExistUrl)
    resolver.exists shouldBe false
  }

  test("exist should return true if this object exist") {
    val resolver = new S3Resolver(existUrl)
    resolver.exists shouldBe true
  }

  test("resolve should throw exception if this object doesnt exist") {
    val resolver = new S3Resolver(nonExistUrl)
    an [JobFile.NotFoundException] should be thrownBy resolver.resolve()
  }

  test("resolve should copy file from S3") {
    val resolver = new S3Resolver(existUrl, "/tmp")
    val file = resolver.resolve()
    file.getPath.endsWith(".jar") shouldBe true
    val bytes = Files.readAllBytes(Paths.get(file.getPath))
    new String(bytes) shouldBe "JAR CONTENT"
  }

}

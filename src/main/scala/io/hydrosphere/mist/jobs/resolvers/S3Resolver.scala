package io.hydrosphere.mist.jobs.resolvers

import java.io.File
import java.nio.file.Paths

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.{AmazonS3ClientBuilder, AmazonS3URI}
import io.hydrosphere.mist.jobs.JobFile


case class S3Resolver(
                       path: String,
                       targetDir: String = "/tmp",
                       credentials: AWSCredentialsProvider = new S3CredentialsProviderChain()
                     ) extends JobResolver {

  private val amazonS3URI = new AmazonS3URI(path)

  private val s3Client = AmazonS3ClientBuilder.standard()
    .withRegion(matchRegion(amazonS3URI.getRegion))
    .withForceGlobalBucketAccessEnabled(true)
    .withCredentials(credentials)
    .build()

  private def isEmpty(x: String) = Option(x).forall(_.isEmpty)

  private def matchRegion(in: String): Regions = in match {
    case in if isEmpty(in) => Regions.DEFAULT_REGION
    case in => Regions.fromName(in)
  }

  override def exists: Boolean = {
    s3Client.doesObjectExist(amazonS3URI.getBucket, amazonS3URI.getKey)
  }

  override def resolve(): File = {
    if (!exists) {
      throw new JobFile.NotFoundException(s"file $path not found")
    }
    val region = s3Client.getRegion.toString
    val bucket = amazonS3URI.getBucket
    val key = amazonS3URI.getKey
    //TODO this unique identifier(region_bucket_key.jar) for jar is it ok?
    val localPath = Paths.get(targetDir, "%s_%s_%s.jar".format(region, bucket, key).toLowerCase)
    //TODO what to do if file exists already? now it overwrites file
    val s3Object = s3Client.getObject(new GetObjectRequest(bucket, key), localPath.toFile)
    new File(localPath.toString)
  }
}

/**
  *  it hack for provide credentials by default if it is not.
  *  It is copy from {@link com.amazonaws.services.s3.S3CredentialsProviderChain}
  *  it is created when we use {@link com.amazonaws.services.s3.AmazonS3ClientBuilder}
  *  without using {@link com.amazonaws.services.s3.AmazonS3ClientBuilder#withCredentials} method
  *  but we have to use this class if we want run it without any credentials some times
  */
class S3CredentialsProviderChain extends DefaultAWSCredentialsProviderChain {

  override def getCredentials(): AWSCredentials = {
    try {
      super.getCredentials()
    } catch {
      case ace: AmazonClientException => {/*NOP*/}
    }
    null
  }
}

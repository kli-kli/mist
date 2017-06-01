package io.hydrosphere.mist.jobs.resolvers

import java.io.File
import java.net.URI

import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.{AmazonS3Client, AmazonS3ClientBuilder}
import io.hydrosphere.mist.jobs.JobFile
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}


class S3Resolver(
    path: String,
    targetDir: String = "/tmp",
    bucket: String = "",
    key: String = ""
  ) extends JobResolver {

  private val uri = new URI(path)


  private val hdfsAddress = s"${uri.getScheme}://${uri.getHost}:${uri.getPort}"

  private lazy val fileSystem = {
    FileSystem.get(new URI(hdfsAddress), new Configuration())
  }



  override def exists: Boolean = {
    //fileSystem.exists(new Path(uri))
    return true
  }

  override def resolve(): File = {
    if (!exists) {
      throw new JobFile.NotFoundException(s"file $path not found")
    }
    val remotePath = new Path(path)
    val checkSum = fileSystem.getFileChecksum(remotePath)

    val localPath = new Path(s"$targetDir/${checkSum.toString}.jar")
    if (!new File(localPath.toString).exists()) {
      fileSystem.copyToLocalFile(false, remotePath, localPath, true)
    }

    new File(localPath.toString)
  }
}

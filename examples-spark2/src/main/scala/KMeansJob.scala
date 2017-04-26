import io.hydrosphere.mist.lib.spark2._
import io.hydrosphere.mist.lib.spark2.ml._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.ml.linalg.Vectors

object KMeansJob extends MLMistJob with SQLSupport {
  def train(savePath: String, datasetPath: String): Map[String, Any] = {
    // Loads data.
    val dataset = session.read.format("libsvm").load(datasetPath)

    // Trains a k-means model.
    val kmeans = new KMeans().setK(2).setSeed(1L)
    val pipeline = new Pipeline().setStages(Array(kmeans))

    val model = pipeline.fit(dataset)

    model.write.overwrite().save(savePath)
    Map.empty
  }

  def serve(modelPath: String, features: List[List[Double]]): Map[String, Any] = {
    import LocalPipelineModel._

    val pipeline = PipelineLoader.load(modelPath)
    val data = LocalData(LocalDataColumn("features", features))

    val result: LocalData = pipeline.transform(data)
    Map("result" -> result.toMapList)
  }
}

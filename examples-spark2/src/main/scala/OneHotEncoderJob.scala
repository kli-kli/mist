import io.hydrosphere.mist.lib.spark2._
import io.hydrosphere.mist.lib.spark2.ml._

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer}
import org.apache.spark.ml.linalg.{Vector => LVector}

object OneHotEncoderJob extends MLMistJob with SQLSupport {
  def train(savePath: String): Map[String, Any] = {
    val df = session.createDataFrame(Seq(
      (0, "a"), (1, "b"), (2, "c"),
      (3, "a"), (4, "a"), (5, "c")
    )).toDF("id", "category")

    val indexer = new StringIndexer()
      .setInputCol("category")
      .setOutputCol("categoryIndex")
      .fit(df)

    val encoder = new OneHotEncoder()
      .setInputCol("categoryIndex")
      .setOutputCol("categoryVec")

    val pipeline = new Pipeline().setStages(Array(indexer, encoder))

    val model = pipeline.fit(df)

    model.write.overwrite().save(savePath)
    Map.empty[String, Any]
  }

  def serve(modelPath: String, features: List[String]): Map[String, Any] = {
    import LocalPipelineModel._

    val pipeline = PipelineLoader.load(modelPath)
    val data = LocalData(LocalDataColumn("category", features))
    val result = pipeline.transform(data)

    val response = result.select("category", "categoryVec").toMapList.map(rowMap => {
      val mapped = rowMap("categoryVec").asInstanceOf[LVector].toArray
      rowMap + ("categoryVec" -> mapped)
    })

    Map("result" -> response)
  }
}

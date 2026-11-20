package models

import org.opencv.core.Point

/** 检测到的一处印章区域（坐标为分析用缩放图上的像素坐标） */
case class SealRegion(x: Int, y: Int, w: Int, h: Int,
                      circularity: Double, area: Double, contour: Array[Point])

/** 单项伪造迹象指标，score 0~100，越高越可疑 */
case class IndicatorResult(key: String, label: String, score: Double, detail: String)

/** 与一枚登记印章的比对结果 */
case class MatchResult(name: String, shapeDist: Double, iou: Double, angle: Int,
                       diffImage: String, verdict: String)

case class SealResult(index: Int, x: Int, y: Int, w: Int, h: Int,
                      indicators: List[IndicatorResult],
                      suspicion: Double, level: String,
                      matches: List[MatchResult])

case class Report(id: String, seals: List[SealResult], overall: Double,
                  summary: String, imageFile: String, annotatedFile: String)

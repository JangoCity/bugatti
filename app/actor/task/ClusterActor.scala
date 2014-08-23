package actor.task

import akka.actor.{Props, ActorLogging, Actor}
import models.task.{TaskCommand, TaskTemplateStep}
import utils.ProjectTask_v

/**
 * Created by jinwei on 21/8/14.
 */
class ClusterActor extends Actor with ActorLogging{
  def receive = {
    case gcc: GenerateClusterCommands => {
      context.actorOf(Props(classOf[EngineActor], 3)) ! ReplaceCommand(gcc.taskObj, gcc.templateStep, gcc.hostname)
    }
    case success: SuccessReplaceCommand => {
      context.parent ! SuccessReplaceCommand(success.commandList)
      context.stop(self)
    }
    case error: ErrorReplaceCommand => {
      log.info(s"cluster errorCommand")
      context.parent ! error
      context.stop(self)
    }
    case gcc: GenerateClusterConfs => {
      context.actorOf(Props(classOf[EngineActor], 15)) ! ReplaceConfigure(gcc.taskObj, gcc.hostname)
    }
    case successConf: SuccessReplaceConf => {
      context.parent ! successConf
    }
    case errorConf: ErrorReplaceConf => {
      context.parent ! errorConf
      context.stop(self)
    }
    case timeout: TimeoutReplace => {
      context.parent ! TimeoutReplace(timeout.key)
      context.stop(self)
    }
    case _ =>
  }
}

case class GenerateClusterCommands(taskId: Int, taskObj: ProjectTask_v, templateStep: Seq[TaskTemplateStep], hostname: String)
case class SuccessReplaceCommand(commandList: Seq[TaskCommand])
case class ErrorReplaceCommand(keys: String)

case class GenerateClusterConfs(envId: Int, projectId: Int, versionId: Int, taskObj: ProjectTask_v, hostname: String)
case class SuccessReplaceConf(taskId: Int, envId: Int, projectId: Int, versionId: Option[Int])
case class ErrorReplaceConf(str: String)

case class TimeoutReplace(key: String)
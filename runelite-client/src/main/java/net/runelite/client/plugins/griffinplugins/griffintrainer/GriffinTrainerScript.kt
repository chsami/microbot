package net.runelite.client.plugins.griffinplugins.griffintrainer

import net.runelite.api.Skill
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.PlayTimer
import net.runelite.client.plugins.griffinplugins.griffintrainer.tasks.CombatTrainerTask
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.Script
import net.runelite.client.plugins.microbot.util.camera.Camera
import java.util.concurrent.TimeUnit

class GriffinTrainerScript : Script() {
    companion object {
        val overallTimer = PlayTimer()
        val taskTimer = PlayTimer()
        var countLabel = ""
        var count = 0
    }

    enum class State {
        RUNNING, STOPPED
    }
    var state = State.STOPPED
    lateinit var config: GriffinTrainerConfig
    private lateinit var combatTrainerTask: CombatTrainerTask

    fun run(config: GriffinTrainerConfig): Boolean {
        this.config = config
        combatTrainerTask = CombatTrainerTask(this.config)

//        login()

        var trainableSkills = setupTrainableSkills()
        var skillToTrain: String? = null

        overallTimer.reset()
        overallTimer.setRandomTimeout(config.minTotalTime(), config.maxTotalTime())
        overallTimer.start()

        Camera.setAngle(45)
        Camera.setPitch(1.0f)

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay({
            if (!super.run()) return@scheduleWithFixedDelay

            try {
                if (overallTimer.isTimerComplete) {
                    endTraining()
                    return@scheduleWithFixedDelay
                }

                if (trainableSkills.isEmpty()) {
                    trainableSkills = setupTrainableSkills()
                    if (trainableSkills.isEmpty()) {
                        endTraining()
                        return@scheduleWithFixedDelay
                    }
                }

                if (skillToTrain == null) {
                    skillToTrain = trainableSkills.removeAt(0)
                }

                if (state == State.STOPPED) {
                    taskTimer.reset()
                    taskTimer.setRandomTimeout(config.minTaskTime(), config.maxTaskTime())
                    taskTimer.start()
                }

                when (skillToTrain) {
                    CombatTrainerTask::class.toString() -> {
                        if (config.trainCombat()) {
                            state = State.RUNNING
                            if (combatTrainerTask.run()) {
                                state = State.STOPPED
                                skillToTrain = null
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                println(ex.message)
            }
        }, 0, 400, TimeUnit.MILLISECONDS)
        return true
    }

    private fun setupTrainableSkills(): MutableList<String> {
        val trainableSkills: MutableSet<String> = mutableSetOf()
        val client = Microbot.getClientForKotlin()

        if (client.getRealSkillLevel(Skill.ATTACK) < config.attackLevel()) {
            trainableSkills.add(CombatTrainerTask::class.toString())
        }

        if (client.getRealSkillLevel(Skill.STRENGTH) < config.strengthLevel()) {
            trainableSkills.add(CombatTrainerTask::class.toString())
        }

        if (client.getRealSkillLevel(Skill.DEFENCE) < config.defenseLevel()) {
            trainableSkills.add(CombatTrainerTask::class.toString())
        }

//        if (client.getRealSkillLevel(Skill.MINING) < config.miningLevel()) {
//            trainableSkills.add(Mining::class.toString())
//        }
//
//        if (client.getRealSkillLevel(Skill.WOODCUTTING) < config.woodcuttingLevel()) {
//            trainableSkills.add(Woodcutting::class.toString())
//        }
//
//        if (client.getRealSkillLevel(Skill.FISHING) < config.fishingLevel()) {
//            trainableSkills.add(Fishing::class.toString())
//        }
//
//        if (client.getRealSkillLevel(Skill.AGILITY) < config.agilityLevel()) {
//            trainableSkills.add(Agility::class.toString())
//        }

        val skillsList = trainableSkills.toMutableList()
        skillsList.shuffle()
        return skillsList
    }

//    private fun login() {
//        if (Alfred.client.getGameState() != GameState.LOGGED_IN) {
//            Alfred.api.account.login(config.worldNumber())
//            Alfred.sleep(2000)
//        }
//    }
//
//    private fun logout() {
//        if (Alfred.client.getGameState() == GameState.LOGGED_IN) {
//            Alfred.api.account.logout()
//        }
//    }

    private fun endTraining() {
//        logout()
        shutdown()
    }
}

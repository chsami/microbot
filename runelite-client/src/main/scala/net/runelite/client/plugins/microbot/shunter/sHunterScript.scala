package net.runelite.client.plugins.microbot.shunter

/*
   Hunting Script
   Sezen

  Hunts shit, so far we have:

  Butterflies
  Salamanders

  With future support for chins coming.

  Sleeps must be changed based off ping, in future a sleep modifier
  will be included in the settings. Ping detection is probably better
  so I may just do that instead :)

 */

import scala.util.Try
import net.runelite.api.{Client, GameObject, GameState, GroundObject, ItemID, MessageNode, ObjectComposition, Skill, Tile, TileObject}
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.ChatMessage
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.Script
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject.getGroundObjects
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.security.Login
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget
import net.runelite.client.plugins.microbot.shunter.enums

import scala.jdk.CollectionConverters._
import java.awt.event.KeyEvent
import java.util.concurrent.TimeUnit
import net.runelite.client.plugins.microbot.shunter.sHunterPlugin
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot
import net.runelite.client.plugins.microbot.shunter.sHunterPlugin.client
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem.getTile
import net.runelite.client.plugins.microbot.util.math.Random
import net.runelite.client.plugins.microbot.util.models.RS2Item
import net.runelite.client.plugins.microbot.util.walker.{Rs2MiniMap, Rs2Walker}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.language.postfixOps



class sHunterScript extends Script {


  def run(config: sHunterConfig): Boolean = {
    Microbot.enableAutoRunOn = true;
    main(client,config)
    return true
  }

  def task(loc: WorldPoint, config: sHunterConfig, traps: List[WorldPoint]): Runnable = new Runnable {
    def run(): Unit = {

      println("Starting...?????????")
      if (!isLoggedIn()) {
        return
      }
       println("Mode: " + config.hunterMode().toString)
      // this fucking sucks! will update w multiple tasks at a later date.
       config.hunterMode().toString match {
        case "Butterflies" => catchButterfly(5556)
        case "Salamander" =>
                            val trapz = new trapId(config)
                            sallyCatcher (loc,traps,trapz)
      }
    }
  }

  def main(client: Client, config: sHunterConfig): Boolean = {
    println("Starting..1232131312313")
    if (!isLoggedIn()) {
      return 
    }
    // This is repulsive, excuse the mess.
    val initialLoc = Rs2Player.getWorldLocation
    val trapz = new trapId(config)
    val trapsLocs: List[WorldPoint] = getInitialTraps(initialLoc,trapz).map(x => x.getWorldLocation)
    println(trapsLocs.length)
    println("trapped")
    // This should vary the task based off selection
    // Thus, >1 task should be implemented
    // to-do later sponge!
    mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay({task(initialLoc,config,trapsLocs)
    },0,1000,TimeUnit.MILLISECONDS)
    true
  }

  def isLoggedIn() : Boolean = {
    Microbot.isLoggedIn()
  }

  // TO-DO SPONGE
   // TRACKING WILL FIND IMPOSTER ID'S BUT FIGURING OUT HOW WE'RE GONNA ACTUALLY TRACK THE BRUSH
   // IS REALLY HARD XD WE COULD JUST CLICK ON ALL BRUSHES WITH RING OF PURSUIT
   // BUT THATS LAME AS FUCK!!!!!!!!!!!!!!!
   //
  /*
/// main tracking //
  ///////////////

  def trackingStart(loc: WorldPoint): Unit = {
    println("Tracking")

    clickBurrow(getBurrow(loc))
    sleep(2000)
    getTracks()
  }

  def getBurrow(loc: WorldPoint): GameObject = {
    Rs2GameObject.get("burrow")

  }

  def clickBurrow(loc: GameObject): Boolean = {
    loc match {
      case null => false
      case _ => Rs2GameObject.interact(loc)
    }
  }
   */
/*
  def getTracks(): Unit = {
    val bushes: List[GameObject] = Rs2GameObject.getGameObjects(19427)
    val tracks: List[TileObject] = Rs2GameObject.getAll.filter(x => Rs2GameObject.convertGameObjectToObjectComposition(x).getImpostor != null)
    val ourTrack = tracks.filter(x => Rs2GameObject.getGameObjectsWithinDistance(1,x.getWorldLocation).filter(x => x.getId == 19427) != null  )
  }
*/


   // salammander catching //

   
  // this may be terrible but i have no idea what to do here lol
  // it works tho
  class trapId(config: sHunterConfig) extends sHunterConfig {
    val opentrap: Int = config.salamanderMode().getOpenTrap
    val fulltrap: Int = config.salamanderMode().getFullTrap
  }


   
  def sallyCatcher(loc: WorldPoint, trapsLocs: List[WorldPoint], trapId: trapId): Unit = {
   println("trap: " + trapId.opentrap)
    println("Sally Catcher")
    dropSally()
    println("Checking................")
    checkTrapStatus(updateTraps(trapsLocs),trapId)

  }

  // drops sally but not always when we have 1 for anti-ban.
  // can be adjusted.
  def dropSally(): Unit = {
    if (Rs2Inventory.count("lizard") >= Random.random(1, 5)) {
      println("releasing..")
      Rs2Inventory.interact("lizard", "release")
      sleep(500, 700)
    }
  }
  // TO-DO SPONGE
  def checkOtherPlayers(loc: WorldPoint): Unit = {
    println("Checking for other players...")
    val otherPlayer = Rs2Player.getPlayers.get(1).getWorldLocation
    otherPlayer match {
      case null => false
      case _    => true
    }
  }

  // Checking trap by trap and running function based off trap's state
  // I thought this was clever
  def checkTrapStatus(traps: List[GameObject], trapIds: trapId): Unit = {
    println("Trap len: " + traps.length)
    traps foreach(x => x.getId match {

        case trapIds.opentrap => println("Our id is: " + x.getId)
                     setTrap(x)
        case trapIds.fulltrap => println("Our id is: " + x.getId)
                     getTrap(x)
        case _ => println("")
        }
      )
    // debug prints abound
    println("Case cleared")
  }

  // set our traps
  // this is not ideal, fugly but it works.
  def setTrap(trap: GameObject): Unit = {
    val ropeCount = Rs2Inventory.count(ItemID.ROPE)
    println("Setting Trap")
    val groundXY: List[Int] = checkGround(trap)
    println("Ground checked...")
    if (groundXY != null) {
      getRopes(groundXY)
    }
    println("why are we breaking exactly?")
    // if we're out of nets, stop.
    if (ropeCount < 1 && (Rs2Inventory.count("small fishing net") < 1)) {
      Microbot.pauseAllScripts = true
    }
      println("Interacting? with: " + trap.getId)
      Rs2GameObject.interact(trap.getWorldLocation)
      sleep(1000)
      sleepUntil(() => !Rs2Player.isMoving && !Rs2Player.isAnimating)
      sleep(2100, 2700)
  }


   // grab our trap

  def getTrap(trap: GameObject) {
    println("Setting Trap")
    val groundXY: List[Int] = checkGround(trap)
    println("Ground checked...")
    if (groundXY != null) {
      getRopes(groundXY)
    }
    sleep(700)
    sleepUntil(() => Rs2GameObject.interact(trap))
   
    val sallyCount = Rs2Inventory.count("salamander")
    // animation for getting traps is wonky and stops animating after run
    // this is the best we got :/
    sleepUntil(() => !Rs2Player.isMoving)
    sleep(500,750)
    sleepUntil(() => !Rs2Player.isAnimating)
    sleep(1650, 2550)
  }
    // grab ropes and fishing net, could be prettier and more consistent.
    def getRopes(groundXY: List[Int]) {
      val ropeCount = Rs2Inventory.count(ItemID.ROPE)

      println("found an empty trap...")
      Rs2GroundItem.interact("rope", "Take", groundXY.head, groundXY.last)
      sleepUntil(() => ropeCount < Rs2Inventory.count(ItemID.ROPE))
      // this sucks vv
      sleep(1200,1750)
      // idk what replaced this interact() vvv
      Rs2GroundItem.interact("small fishing net", "Take", groundXY.head, groundXY.last)
      sleep(800)
    }



   // should keep our available traps static
   // grabs at init

  val getInitialTraps: (WorldPoint, trapId) => List[GameObject] = (loc: WorldPoint, traps: trapId) => {
    println("Updating..." + loc.toString)
    // adding range later, too lazy atm
   Rs2GameObject.getGameObjectsWithinDistance(10,loc).filter(x => (x.getId == traps.opentrap) || (x.getId == traps.fulltrap)).toList
  }

   // reloading our traps objectid's at worldpoint where traps were initially found
  def updateTraps(trapsLocs: List[WorldPoint]): List[GameObject] = {
    print("UPDATING")
    trapsLocs.take(checkTraps()).map(x => Rs2GameObject.getGameObject(x))
  }

  // Checking for items at all X(-1 -> 1), Y(-1 -> 1) for supplied (empty) traps
   // this could be reduced to one for loop probably but it works and im lazy atm
  def checkGround(trap: GameObject): List[Int] = {
    val trapLoc = trap.getWorldLocation
    println("X: " + trapLoc.getX + " Y : " + trapLoc.getY)
    for (i: Int <- -1 to 1) {
      if (Rs2GroundItem.getAllAt(trapLoc.getX + i, trapLoc.getY).nonEmpty) {
        println("found one")
        return List(trapLoc.getX + i, trapLoc.getY)
      }
    }
    for (i: Int <- -1 to 1) {
      if (Rs2GroundItem.getAllAt(trapLoc.getX, trapLoc.getY + i).nonEmpty) {
        return List(trapLoc.getX, trapLoc.getY + i)
      }
    }
    null
  }

  // Checking our skill level so we only place the allowed trap count
  def checkTraps() : Int = {
    client.getRealSkillLevel(Skill.HUNTER) match {
      case x if 1 until 20 contains x => 1
      case x if 21 until 40 contains x => 2
      case x if 40 until 60 contains x => 3
      case x if 60 until 80 contains x => 4
      case x if 80 until 100 contains x => 5
    }
  }


  def shutDown() = super.shutdown()


  // this is really basic but it works well.
  // will add more butterflies later but who catches them after 29 anyway?
  def catchButterfly(id: Int): Unit = {
    println("Test?)")
    if (client.getEnergy() > Random.random(200,500)) {
      Rs2Player.toggleRunEnergy(true)
    }
    Rs2Npc.interact(Rs2Npc.getNpc(id),"Catch")
    sleepUntil(() => !Rs2Player.isInteracting && !Rs2Player.isMoving,20000)
    sleep(820,2000)
  }

  // we don't need jars but in-case some1 wants it, it's here.
  def checkJars(): Unit = {
    println("Jars checked...?")
    if (Rs2Inventory.hasItem(10020)) {
      return
    }
    else {
      println("Interact:")
      Rs2Inventory.interact(10020,"release")
      sleep(800,1000)
      return
      }
    }
  }



object sHunterScript {
  val version : Double = 1.0;
  import sHunterScript._
  print("test")


}

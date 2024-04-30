# Development

---

## Setup development environment

---

Since Microbot is a fork of runelite you can simply follow the runelite wiki on how to use intelij to setup a development environment

https://github.com/runelite/runelite/wiki/Building-with-IntelliJ-IDEA

## Project Structure

---

Everything about microbot can be found in the plugin folder called "microbot"

**runelite-client\src\main\java\net\runelite\client\plugins\microbot**

![img_3.png](img_3.png)

Everything that will help you make scripts is under the **util** folder

![img_4.png](img_4.png)

## API

---

Everything that will help you make scripts is under the **util** folder

* Rs2AntiBan
* Rs2Bank
* Rs2Camera
* Rs2Combat
* Rs2Dialogues
* Rs2Equipment
* Rs2Cannon
* Rs2GameObject
* Rs2GroundItem
* Rs2Inventory
* Rs2Keyboard
* Rs2Magic
* Rs2Spells
* Rs2Food
* Rs2Npc
* Rs2Player
* Rs2Pvp
* Rs2Prayer
* Rs2Reflection
* Rs2Settings
* Rs2Tab
* Rs2Walker
* Rs2MiniMap
* Rs2Widget

## Examples

Inside the microbot plugin folder you'll see a folder called "example"

![img_5.png](img_5.png)

You can open the examplescript.java. This is the main file we'll be working in.

![img_6.png](img_6.png)

### Combat

---

Lets try to make a script that attacks a man.

![img_7.png](img_7.png)

To attack an npc we'll have to look into our Rs2Npc utility class.


```java
Rs2Npc.attack("man")
```

Our example script should look like this now.

```java
public class ExampleScript extends Script {
    public static double version = 1.0;

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                long startTime = System.currentTimeMillis();

                //SCRIPT CODE COMES HERE

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
```

The script should now attack the man

![img_9.png](img_9.png)

Congratulations, you just made a simple auto fighter! 🥳🥳

### Skills

* Fishing

Coming soon!

### Utility

* Alching

Coming soon!

### Drawing

Coming soon!

 ### UI Configs

Coming soon!

---

**Are you stuck? Join our [Discord](https://discord.gg/zaGrfqFEWE) server.**


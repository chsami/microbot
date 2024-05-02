## Auto Fighter

Lets try to make a script that attacks a man.

### Attack Npc

---


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
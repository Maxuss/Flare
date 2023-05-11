---
title: Flare Basics
---

# Flare Basics

Before getting started with the UI part of Flare, you should get known with 
the Reactivity of Flare.

## Reactive States

Reactive states are thread-safe mutable value containers that emit events once their
value changes. Look at this example:

```java
var myInt = new ReactiveState<>(0); // Create a new reactive state
// Subscribes to state change   
myInt.subscribe(value -> System.out.println("New value! %s".formatted(value)));    
myInt.set(1); // sets value of this state to 1
myInt.set(0); // sets value of this state to 0
assert myInt.get() == 0; // true!
```

Outputs:

```
New value! 1
New value! 0
```

Let's take a closer look at what happens with event emitting.

ReactiveState implements the `ReactiveNotifier<V>` interface, 
that allows it to subscribe handlers to itself.
Here is the signature of the `ReactiveNotifier#subscribe` method:
```java
// simplified 
ReactiveSubscriber<V> subscribe(ReactiveSubscriber<V> subscriber);
```

It takes a ReactiveSubscriber that handles state change.
ReactiveSubscriber is a FunctionalInterface, however lots of elements in 
Flare implement it for better experience.

`subscribe` is similar to the `useEffect` hook in React in the way that it allows to listen
for state changes.

## Items in Flare

You will rarely be using raw ItemStacks in Flare for UI item representation,
instead it introduces the `ItemProvider` interface!

As with many other interfaces in Flare, ItemProvider is by default a *functional interface*. Its
signature is extremely simple:

```java
ItemStack provide();
```

However, it has a few implementations for convenience:

### StillItemProvider
`StillItemProvider` is an item provider that takes in an ItemStack and always returns it. 
As simple as that. It might be useful for placeholders or static item representations.

```java
// Two methods of acquiring
ItemStack myStack = ...;
ItemProvider stillProvider = ItemProvider.still(myStack); // recommended
ItemProvider stillProvider2 = new StillItemProvider(myStack); // alternative
```

### ReactiveItemProvider
`ReactiveItemProvider<V>` is the item provider you will most likely be using a lot in Flare.
It implements the `ReactiveSubscriber<V>` interface, which makes it recalculate each time a state changes.

ReactiveItemProvider takes in a closure that provides an ItemStack based on state value.

```java
// Two methods of acquring
// recommended:
ReactiveItemProvider<String> = ItemProvider.reactive(text ->
                                    Items.withName(Material.DIAMOND, text));
// alternative:
ReactiveItemProvider<String> = new ReactiveItemProvier<>(text -> 
                                    Items.withName(Material.DIAMOND, text));

```

### The `Items` utility class
The `Items` utility class is used to simplify the creation of items. It allows creating items
with a certain ItemMeta, lore, name, head skin (for player heads), etc. It also has a shortcut
to create ItemStack builders.

Here are a few examples of the `Items` utility class:

```
ItemStack headStack = Items.head("<base64 head skin here>");
ItemStack another = Items.withMeta(Material.DIAMOND,
                                       meta -> meta.setUnbreakable(true));
// Supports lenient MiniMessage!
ItemStack withName = Items.withName(Material.DIAMOND, "<red>My Cool Diamond"); 
// And PlaceholderAPI! (if present)
ItemStack withNamePlaceholder = Items
        .withName(Material.DIAMOND, "<gold>%player_name%'s Diamond!", player);
// With lore (automatically partitioned)
ItemStack withLore = Items
        .withLore(Material.DIAMOND, "This is some <gold>very</gold> long lore! its very very long")
```

## ReactiveComponent

ReactiveComponent is a Kyori Adventure `Component` wrapper that implements 
`ReactiveSubscriber` and therefore can depend on a ReactiveState.

Reactive components are easy to use:

```java
var myString = new ReactiveState<>("");
ReactiveComponent<String> component = ReactiveComponent
        .reactive(text -> FlareUtil.text("<red>Hello %s!".formatted(text)));
myString.subscribe(component);
```

Note, that simply subscribing does not initially populate the component.
This issue is addressed in the `Reactive` utility class.

## The `Reactive` utility class

The `Reactive` utility class provides a few methods that help with creating
reactive subscribers.

For example:

```java
// Before:
ReactiveState<String> myState = ...;
var itemProvider = new ReactiveItemProvider(text -> 
                            Items.withName(Material.DIAMOND, text));
myState.subscribe(itemProvider);
itemProvider.onStateChange(myState.get()); // initially populating item data

// After:
ReactiveState<String> myState = ...;
var itemProvider = Reactive.item(myState, text ->
                            Items.withName(Material.DIAMOND, text));
// The same can be done for text via Reactive#text
```

As you can see, `Reactive` handles a lot of boilerplate logic, which lets
you write more meaningful and readable code.

## The `FlareUtil` utility class
This is a general utility class with methods that are used by Flare internally, but may
be useful for your needs too. There are a lot of methods here, so I recommend looking at javadocs
or source code. However, here are the ones that are more likely to pose interest to you.

```java
// Executes a task on shared thread pool
void execute(Runnable runnable); 

// Parses minimessage into component
Component text(String text); 

// Parses PlaceholderAPI (if available) and minimessage
Component text(String text, Player player); 

// Memoizes a Computable that may later be passed to different handlers
Computable<I, O> memoize(Computable<I, O> producer);

// Attempts to partition a single string into multiple strings with size
// roughly equal to `size`. Useful for lore formatting.
List<String> partitionString(String origin, int size);

// Applies fancy formatting to float, based on decimal number count
String formatFloat(float num);

// Returns true if provided ItemStack is either null or air
boolean isNullOrAir(ItemStack stack);
```

## Conclusion

This was introduction to basics of reactivity and other utility methods inside Flare. The
next pages will contain information about building UI.
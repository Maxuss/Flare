---
title: Hooks
---

Hooks are special methods within Frames that help building UIs more easily. Here are all of them:

## useState

`useState` is present both in [FunctionComposables](/ui/composable#functioncomposable) *and* Frames.
Within FunctionComposable it returns a composable-bound state that marks component as dirty on value change,
within Frames it returns a simple reactive state.

```java
ReactiveState<String> myString = useState("Hello, World!");
```

## useUnboundState

`useUnboundState` is present in FunctionComposables and allows to create an unbound state. The component
will not be re-rendered on state value change.

## useContext

`useContext` is present only in Frames, and it allows to set context to certain value. Context will
be passed down to all components, and they will be able to retrieve it with the `context` method.

```java
useContext("Hello, World!");
// ---
@Nullable String ctx = contextOrNull(); // (1)!
@NotNull String ctx = context(); // (2)!
```

1. Will return null if context is not of type `String`
2. Will throw InvalidContextValue if context is not of type `String`

## setTitle

`setTitle` allows to set title within Frames.

```java
setTitle(player, "Hello, this is new title!");
```

## useMemo

`useMemo` is used to memoize a `Computable<I, O>`

```java
Computable<Integer, String> memoized = useMemo(myState, integer -> {
   // ... some heavy computations here
})
```
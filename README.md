Trident-ML is a distributed realtime online machine learning. It allows you to build real time predictive features using scalable online algotihms.
This library is built on top of [Storm](https://github.com/nathanmarz/storm), a distributed stream processing framework which runs on a cluster of machines and supports horizontal scaling.

Trident-ML currently supports : 
* Linear classification (Perceptron, Passive-Aggresive, Winnow, AROW)
* Linear regression (Perceptron, Passive-Aggresive)
* Feature scaling (standardization, normalization)
* Text feature extraction
* Stream statistics (mean, variance)

# API Overview

Trident-ML is based on [Trident](https://github.com/nathanmarz/storm/wiki/Trident-tutorial), a high-level abstraction for doing realtime computing.
If you're familiar with high level batch processing tools like Pig or Cascading, the concepts of Trident will be very familiar.

## Create instances

Trident-ML process unbounded streams of data implemented by an infinite collection of [Instance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/core/Instance.java) or [TextInstance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/core/TextInstance.java).
Creating instances is the first step to build a prediction tools.
Trident-ML offers [Trident functions](https://github.com/nathanmarz/storm/wiki/Trident-API-Overview#functions) to convert Trident tuples to instances :

* Use [InstanceCreator](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/preprocessing/InstanceCreator.java) to create [Instance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/core/Instance.java)

```java
TridentTopology toppology = new TridentTopology();

toppology
  // Emit tuples with 2 random features (named x0 and x1) and an associated boolean label (named label)
  .newStream("randomFeatures", new RandomFeaturesSpout())
  
  // Transform trident tupl to instance
  .each(new Fields("label", "x0", "x1"), new InstanceCreator<Boolean>(), new Fields("instance"));
```

* Use [TextInstanceCreator](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/preprocessing/TextInstanceCreator.java) to create [TextInstance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/core/TextInstance.java)

```java
TridentTopology toppology = new TridentTopology();

toppology
  // emit tuples containing text and associated label (topic)
  .newStream("reuters", new ReutersBatchSpout())

  // Convert trident tupl to text instance
  .each(new Fields("label", "text"), new TextInstanceCreator<Integer>(), new Fields("instance"));
```

## Build classifier

## Build regressor

## Stream statistics

## Preprocessing data


# Upcoming features
* Clustering (KMeans)
* Noise adaptive filter (LMS, Wiener, Kalman, ...)
* Nonlinear classification (Online Kernel trick, Online SVM, Online Decision Tree, ...)
* Image features extraction
* Change detection

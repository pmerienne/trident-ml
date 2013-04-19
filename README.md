# Trident-ML – the Mahout of stream processing

Trident-ML is a distributed realtime online machine learning. It allows you to build real time predictive features using scalable online algotihms.
This library is built on top of [Storm](https://github.com/nathanmarz/storm), a distributed stream processing framework which runs on a cluster of machines and supports horizontal scaling.

Trident-ML currently supports : 
* Linear classification (Perceptron, Passive-Aggresive, Winnow, AROW)
* Linear regression (Perceptron, Passive-Aggresive)
* Stream statistics (mean, variance)
* Feature scaling (standardization, normalization)
* Text feature extraction

## API Overview

Trident-ML is based on a high-level abstraction for doing realtime computing :  [Trident](https://github.com/nathanmarz/storm/wiki/Trident-tutorial).
If you're familiar with high level batch processing tools like Pig or Cascading, the concepts of Trident will be very familiar.

### Create instances

Trident-ML process unbounded streams of data implemented by an infinite collection of [Instance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/core/Instance.java) or [TextInstance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/core/TextInstance.java).
Creating instances is the first step to build a prediction tools.
Trident-ML offers 2 [Trident functions](https://github.com/nathanmarz/storm/wiki/Trident-API-Overview#functions) to convert Trident tuples to instances :
* [InstanceCreator](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/preprocessing/InstanceCreator.java)
* [TextInstanceCreator](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/preprocessing/TextInstanceCreator.java)

For the purposes of illustration, this example will read an infinite stream of random features and convert them to instances :
```java
TridentTopology toppology = new TridentTopology();

toppology
  // emit tuples with random features named x0, x1, x2, ..., x5
  .newStream("originalStream", new RandomFeaturesSpout())
  
  // Transform trident tupl to instance
  .each(new Fields("x0", "x1", "x2", "x3", "x4", "x5"), new InstanceCreator(), new Fields("instance"));
```


# Upcoming features
* Clustering (KMeans)
* Noise adaptive filter (LMS, Wiener, Kalman, ...)
* Nonlinear classification (Online Kernel trick, Online SVM, Online Decision Tree, ...)
* Image features extraction
* Change detection

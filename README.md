Trident-ML is a distributed realtime online machine learning library. It allows you to build real time predictive features using scalable online algotihms.
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
  
  // Transform trident tuple to instance
  .each(new Fields("label", "x0", "x1"), new InstanceCreator<Boolean>(), new Fields("instance"));
```

* Use [TextInstanceCreator](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/preprocessing/TextInstanceCreator.java) to create [TextInstance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/core/TextInstance.java)

```java
TridentTopology toppology = new TridentTopology();

toppology
  // Emit tuples containing text and associated label (topic)
  .newStream("reuters", new ReutersBatchSpout())

  // Convert trident tuple to text instance
  .each(new Fields("label", "text"), new TextInstanceCreator<Integer>(), new Fields("instance"));
```

## Build classifier

## Build regressor

## Stream statistics
Stream statistics such as mean, standard deviation and count can be easily computed using Trident-ML.
Theses statistics are stored in a [StreamStatistics](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/stats/StreamStatistics.java) object.
Statistics update and query are performed respectively using a [StreamStatisticsUpdater](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/stats/StreamStatisticsUpdater.java) and a [StreamStatisticsQuery](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/stats/StreamStatisticsQuery.java).


```java
TridentTopology toppology = new TridentTopology();

// Update stream statistics
TridentState streamStatisticsState = toppology
  // emit tuples with random features
  .newStream("randomFeatures", new RandomFeaturesSpout())

  // Transform trident tuple to instance
  .each(new Fields("x0", "x1"), new InstanceCreator(), new Fields("instance"))

  // Update stream statistics
  .partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new StreamStatisticsUpdater("randomFeaturesStream", StreamStatistics.fixed()));

// Query stream statistics (with DRPC)
toppology.newDRPCStream("queryStats", localDRPC)
  // Query stream statistics
  .stateQuery(streamStatisticsState, new StreamStatisticsQuery("randomFeaturesStream"), new Fields("streamStats"));

```
Note that Trident-ML can suppport concept drift in a sliding window manner.
Use StreamStatistics#adaptive(maxSize) instead of StreamStatistics#fixed() to construct StreamStatistics implementation with a maxSize length window.


## Preprocessing data
Data preprocessing is an important step in the data mining process. 
Trident-ML provides Trident functions to transform raw features into a representation that is more suitable for machine learning algorithms.

* [Normalizer](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/preprocessing/Normalizer.java) scales individual instances to have unit norm. 

```java
TridentTopology toppology = new TridentTopology();

toppology
  // Emit tuples with 2 random features (named x0 and x1) and an associated boolean label (named label)
  .newStream("randomFeatures", new RandomFeaturesSpout())

  // Convert trident tuple to instance
  .each(new Fields("label", "x0", "x1"), new InstanceCreator<Boolean>(), new Fields("instance"))
	  
  // Scales features to unit norm
  .each(new Fields("instance"), new Normalizer(), new Fields("scaledInstance"));
```

* [StandardScaler](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/preprocessing/StandardScaler.java) transform raw features to standard normally distributed data (Gaussian with zero mean and unit variance). It uses [Stream Statistics](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/storm/trident/ml/stats/StreamStatistics.java) to remove mean and scale to variance.

```java
TridentTopology toppology = new TridentTopology();

toppology
  // Emit tuples with 2 random features (named x0 and x1) and an associated boolean label (named label)
  .newStream("randomFeatures", new RandomFeaturesSpout())

  // Convert trident tuple to instance
  .each(new Fields("label", "x0", "x1"), new InstanceCreator<Boolean>(), new Fields("instance"))
		  
  // Update stream statistics
  .partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new StreamStatisticsUpdater("streamStats", new StreamStatistics()), new Fields("instance", "streamStats")).newValuesStream()

  // Standardized stream using original stream statistics
  .each(new Fields("instance", "streamStats"), new StandardScaler(), new Fields("scaledInstance"));
```

# Upcoming features
* Clustering (KMeans)
* Noise adaptive filter (LMS, Wiener, Kalman, ...)
* Nonlinear classification (Online Kernel trick, Online SVM, Online Decision Tree, ...)
* Image features extraction
* Change detection

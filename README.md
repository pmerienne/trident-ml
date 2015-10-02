Trident-ML is a realtime online machine learning library. It allows you to build real time predictive features using scalable online algorithms.
This library is built on top of [Storm](https://github.com/nathanmarz/storm), a distributed stream processing framework which runs on a cluster of machines and supports horizontal scaling.
The packaged algorithms are designed to fit into limited memory and processing time but they don't work in a distributed way.

Trident-ML currently supports : 
* Linear classification (Perceptron, Passive-Aggressive, Winnow, AROW)
* Linear regression (Perceptron, Passive-Aggressive)
* Clustering (KMeans)
* Feature scaling (standardization, normalization)
* Text feature extraction
* Stream statistics (mean, variance)
* Pre-Trained Twitter sentiment classifier

# API Overview

Trident-ML is based on [Trident](https://github.com/nathanmarz/storm/wiki/Trident-tutorial), a high-level abstraction for doing realtime computing.
If you're familiar with high level batch processing tools like Pig or Cascading, the concepts of Trident will be very familiar.

It's recommended to read the [Storm and Trident documentation](https://github.com/nathanmarz/storm/wiki/Documentation).

## Create instances

Trident-ML process unbounded streams of data implemented by an infinite collection of [Instance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/core/Instance.java) or [TextInstance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/core/TextInstance.java).
Creating instances is the first step to build a prediction tools.
Trident-ML offers [Trident functions](https://github.com/nathanmarz/storm/wiki/Trident-API-Overview#functions) to convert Trident tuples to instances :

* Use [InstanceCreator](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/preprocessing/InstanceCreator.java) to create [Instance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/core/Instance.java)

```java
TridentTopology toppology = new TridentTopology();

toppology
  // Emit tuples with 2 random features (named x0 and x1) and an associated boolean label (named label)
  .newStream("randomFeatures", new RandomFeaturesSpout())
  
  // Transform trident tuple to instance
  .each(new Fields("label", "x0", "x1"), new InstanceCreator<Boolean>(), new Fields("instance"));
```

* Use [TextInstanceCreator](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/preprocessing/TextInstanceCreator.java) to create [TextInstance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/core/TextInstance.java)

```java
TridentTopology toppology = new TridentTopology();

toppology
  // Emit tuples containing text and associated label (topic)
  .newStream("reuters", new ReutersBatchSpout())

  // Convert trident tuple to text instance
  .each(new Fields("label", "text"), new TextInstanceCreator<Integer>(), new Fields("instance"));
```

## Supervised classification
Trident-ML includes differents algorithms to do supervised classification : 
* [PerceptronClassifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/PerceptronClassifier.java)
implements a binary classifier based on an averaged kernel-based perceptron.
* [WinnowClassifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/WinnowClassifier.java)
implements [Winnow algorithm](http://link.springer.com/content/pdf/10.1007%2FBF00116827.pdf).
It scales well to high-dimensional data and performs better than a perceptron when many dimensions are irrelevant. 
* [BWinnowClassifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/BWinnowClassifier.java)
 is an implementation of the [Balanced Winnow algorithm](http://link.springer.com/content/pdf/10.1007%2FBF00116827.pdf) 
an extension of the original Winnow algorithm.
* [AROWClassifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/AROWClassifier.java)
is an simple and efficient implementation of [Adaptive Regularization of Weights](http://books.nips.cc/papers/files/nips22/NIPS2009_0611.pdf).
It combines several useful properties : large margin training, confidence weighting, and the capacity to handle non-separable data.
* [PAClassifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/PAClassifier.java)
implements the [Passive-Aggressive binary classifier](http://eprints.pascal-network.org/archive/00002147/01/CrammerDeKeShSi06.pdf)
a margin based learning algorithm.
* [MultiClassPAClassifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/MultiClassPAClassifier.java)
a variant of the Passive-Aggressive performing one-vs-all multiclass classification.

Theses classifiers learn from a datastream of labeled [Instance](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/core/Instance.java)
using a [ClassifierUpdater](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/ClassifierUpdater.java).
Another datastream of unlabeled instance can be classified with a [ClassifyQuery](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/classification/ClassifyQuery.java).

The following example learn a NAND function and classify instances comming from a DRPC stream.

```java
TridentTopology toppology = new TridentTopology();

// Create perceptron state from labeled instances stream
TridentState perceptronModel = toppology
  // Emit tuple with a labeled instance of enhanced NAND features
  // i.e. : {label=true, features=[1.0 0.0 1.0]} or {label=false, features=[1.0 1.0 1.0]}  
  .newStream("nandsamples", new NANDSpout())
				
  // Update perceptron
  .partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new ClassifierUpdater<Boolean>("perceptron", new PerceptronClassifier()));

// Classify instance from a DRPC stream
toppology.newDRPCStream("predict", localDRPC)
  // Transform DRPC ARGS to unlabeled instance
  .each(new Fields("args"), new DRPCArgsToInstance(), new Fields("instance"))

  // Classify instance using perceptron state
  .stateQuery(perceptronModel, new Fields("instance"), new ClassifyQuery<Boolean>("perceptron"), new Fields("prediction"));
```				

Trident-ML provides the [KLDClassifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/nlp/KLDClassifier.java)
which implements a [text classifier using the Kullback-Leibler Distance](http://lvk.cs.msu.su/~bruzz/articles/classification/Using%20Kullback-Leibler%20Distance%20for%20Text%20Categorization.pdf).

Here's the code to build a news classifier using Reuters dataset :

```java
TridentTopology toppology = new TridentTopology();

// Create KLD classifier state from labeled instances stream
TridentState classifierState = toppology
  // Emit tuples containing text and associated label (topic)
  .newStream("reuters", new ReutersBatchSpout())

  // Convert trident tuple to text instance
  .each(new Fields("label", "text"), new TextInstanceCreator<Integer>(), new Fields("instance"))
  
  // Update classifier
  .partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new TextClassifierUpdater("newsClassifier", new KLDClassifier(9)));

// Classification stream
toppology.newDRPCStream("classify", localDRPC)

  // Convert DRPC args to text instance
  .each(new Fields("args"), new TextInstanceCreator<Integer>(false), new Fields("instance"))

  // Query classifier with text instance
  .stateQuery(classifierState, new Fields("instance"), new ClassifyTextQuery("newsClassifier"), new Fields("prediction"));
```

## Unsupervised classification
[KMeans](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/clustering/KMeans.java)
is an implementation of the well known [k-means algorithm](http://en.wikipedia.org/wiki/K-means_clustering)
which partitions instances into clusters.

Use a [ClusterUpdater](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/clustering/ClusterUpdater.java)
or a [ClusterQuery](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/clustering/ClusterQuery.java)
to respectively udpate clusters or query the clusterer :

```java
TridentTopology toppology = new TridentTopology();

// Training stream
TridentState kmeansState = toppology
  // Emit tuples with a instance containing an integer as label and 3 double features named (x0, x1 and x2)
  .newStream("samples", new RandomFeaturesForClusteringSpout())

  // Convert trident tuple to instance
  .each(new Fields("label", "x0", "x1", "x2"), new InstanceCreator<Integer>(), new Fields("instance"))

  // Update a 3 classes kmeans
  .partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new ClusterUpdater("kmeans", new KMeans(3)));

// Cluster stream
toppology.newDRPCStream("predict", localDRPC)
  // Convert DRPC args to instance
  .each(new Fields("args"), new DRPCArgsToInstance(), new Fields("instance"))

  // Query kmeans to classify instance
  .stateQuery(kmeansState, new Fields("instance"), new ClusterQuery("kmeans"), new Fields("prediction"));
```

## Stream statistics
Stream statistics such as mean, standard deviation and count can be easily computed using Trident-ML.
Theses statistics are stored in a [StreamStatistics](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/stats/StreamStatistics.java) object.
Statistics update and query are performed respectively using a [StreamStatisticsUpdater](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/stats/StreamStatisticsUpdater.java) and a [StreamStatisticsQuery](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/stats/StreamStatisticsQuery.java) :

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

* [Normalizer](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/preprocessing/Normalizer.java) scales individual instances to have unit norm. 

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

* [StandardScaler](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/preprocessing/StandardScaler.java) transform raw features to standard normally distributed data (Gaussian with zero mean and unit variance). It uses [Stream Statistics](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/stats/StreamStatistics.java) to remove mean and scale to variance.

```java
TridentTopology toppology = new TridentTopology();

toppology
  // Emit tuples with 2 random features (named x0 and x1) and an associated boolean label (named label)
  .newStream("randomFeatures", new RandomFeaturesSpout())

  // Convert trident tuple to instance
  .each(new Fields("label", "x0", "x1"), new InstanceCreator<Boolean>(), new Fields("instance"))
		  
  // Update stream statistics
  .partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new StreamStatisticsUpdater("streamStats", new StreamStatistics()), new Fields("instance", "streamStats")).newValuesStream()

  // Standardize stream using original stream statistics
  .each(new Fields("instance", "streamStats"), new StandardScaler(), new Fields("scaledInstance"));
```

## Pre-trained classifier

Trident-ML includes a pre-trained [twitter sentiment classifier](https://github.com/pmerienne/trident-ml/blob/master/src/main/java/com/github/pmerienne/trident/ml/nlp/TwitterSentimentClassifier.java).
It was built on a subset of the [Twitter Sentiment Corpus by Niek Sanders](http://www.sananalytics.com/lab/twitter-sentiment/) with a multi class PA classifier and classifies raw tweets as positive (true) or negative (false).
This classifier is implemented as a trident function and can be easily used in a trident topology : 

```java
TridentTopology toppology = new TridentTopology();

// Classification stream
toppology.newDRPCStream("classify", localDRPC)
  // Query classifier with text instance
  .each(new Fields("args"), new TwitterSentimentClassifier(), new Fields("sentiment"));
```

# Maven integration : 

Trident-Ml is hosted on Clojars (a Maven repository). 
To include Trident-ML in your project , add the following to your pom.xml: : 
 ```xml
 <repositories>
	<repository>
		<id>clojars.org</id>
		<url>http://clojars.org/repo</url>
	</repository>
</repositories>

<dependency>
	<groupId>com.github.pmerienne</groupId>
	<artifactId>trident-ml</artifactId>
	<version>0.0.4</version>
</dependency>
 ```

# Does trident-ml support distributed learning?
Storm allows trident-ml to process batches of tuples in a distributed way (batches will be computed among several nodes). This means that trident-ml can scale horizontally with workload.

However Storm prevents state updates to append simultaneously and the model learning is done in a state update. That's why, the learning step can't be distributed. Thankfully this lack of parallelization isn't a real bottle neck because the incremental algorithms are very fast (and simple!).

Distributed algorithms will not be implemented in trident-ml, the whole design prevents this. 

So you can't achieve distributed learning however but you can still partition the streams to pre-process/enrich your data in a distributed manner.

# Copyright and license

Copyright 2013-2015 Pierre Merienne

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

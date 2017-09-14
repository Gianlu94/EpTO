Distributed systems 2 project -- EpTO

Da riportare nel report: nel paper non si accenna al problema che sorge quando due eventi e1 ed e2 aventi lo stesso timestamp, a causa della disallineazione dei round dei nodi, hanno TTL diverso; questo causa il fatto che uno dei due eventi è consegnabile un round prima dell'altro, anche se l'altro ha timestamp uguale ma sourceId minore. Al round successivo l'altro evento verrà in ogni caso consegnato perché ha timestamp uguale a lastDeliveredTimestamp

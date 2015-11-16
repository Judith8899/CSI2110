public class DecisionTree {

    private DecisionStump root;

    public DecisionTree(int dimension, int numberOfClasses) {
        DecisionStump.nClasses = numberOfClasses;
        DecisionStump.dim = dimension;
        root = new DecisionStump();
    }

    // returns the root of the decision tree
    public DecisionStump getRoot() {

        return root;
    }

    // get the class associated with this vector according to the decision tree
    public int getDecision(double[] vector) {
        return root.getDecision(vector);
    }

    // replace a leaf node by a DecisionStump with two leaves
    public void replace(DecisionStump leaf, int featureIndex, double threshold) {
        if (!leaf.isExternal()) {
            throw new RuntimeException("Leaf being replaced must be external!");
        }

        DecisionStump replacement = new DecisionStump(featureIndex, threshold);
        replacement.setGreaterBranch(new DecisionStump());
        replacement.setSmallerBranch(new DecisionStump());

        DecisionStump parent = leaf.getParent();
        if (parent != null) {
            if (parent.getSmallerBranch() == leaf) {
                parent.setSmallerBranch(replacement);
            } else {
                parent.setGreaterBranch(replacement);
            }
        } else {
            this.root = replacement;
        }

    }

    // gets the leaf with the smallest maximal class probability
    public DecisionStump getSmallestMaxProb() {
        DecisionStump smallest = getSmallestMaxProbRecursive(this.root, null);
        if (!smallest.isExternal()) {
            System.out.println("Uh oh how did we get here??");
        }
        return smallest;
    }

    private DecisionStump getSmallestMaxProbRecursive(DecisionStump current, DecisionStump smallest) {
        if(!current.isExternal()) {
            DecisionStump left = getSmallestMaxProbRecursive(current.getSmallerBranch(), smallest);
            DecisionStump right = getSmallestMaxProbRecursive(current.getGreaterBranch(), smallest);
            return left.getMaxProb() < right.getMaxProb() ? left : right;
        }
        return (smallest == null || current.getMaxProb() < smallest.getMaxProb()) ? current : smallest;
    }


    // updates the probability count of each node of the decision tree
    // based on a sample for which the associated class is known
    public void train(double[] vector, int classNumber) {
        root.updateProbCount(vector, classNumber);
    }

    // reset the probability counts of all nodes
    public void resetAll() {
        reset(root);
    }

    private void reset(DecisionStump ds) {

        ds.resetProbCount();

        if (!ds.isExternal()) {

            ds.getSmallerBranch().resetProbCount();
            ds.getGreaterBranch().resetProbCount();
        }
    }

    // pre-order print of all nodes
    public void print() {
        preOrderPrint(this.root);
    }

    private void preOrderPrint(DecisionStump stump) {
        System.out.printf("%s max probability class is %d with %.2f%%%n", stump, stump.getMaxClass(), (stump.getMaxProb() * 100));
        DecisionStump left, right;
        if ((left = stump.getSmallerBranch()) != null) {
            preOrderPrint(left);
        }
        if ((right = stump.getGreaterBranch()) != null) {
            preOrderPrint(right);
        }
    }
}
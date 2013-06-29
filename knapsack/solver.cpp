#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <vector>
#include <algorithm>
#include <functional>
#include <stdexcept>
#include <numeric>
#include <memory.h>
#include <unordered_map>
#include <unordered_set>

using std::cerr;
using std::cin;
using std::cout;
using std::endl;
using std::vector;
using std::sort;
using std::string;

struct Item
{
  int value, weight;
  int id;
};

bool compareByValuePerWeight(const Item& lhs, const Item& rhs)
{
  return double(lhs.value) / lhs.weight > double(rhs.value) / rhs.weight;
}

bool compareByWeight(const Item& lhs, const Item& rhs)
{
  return lhs.weight < rhs.weight;
}

bool compareByValue(const Item& lhs, const Item& rhs)
{
  return lhs.value > rhs.value;
}

class ItemsPreprocessor
{
public:
  virtual vector<Item> preprocess(vector<Item> items) = 0;

  virtual string name() { return "ItemsPreprocessor"; }
};

class SortItemsByValuePerWeightPreprocessor : public ItemsPreprocessor
{
public:
  vector<Item> preprocess(vector<Item> items)
  {
    sort(items.begin(), items.end(), compareByValuePerWeight);
    return items;
  }

  string name() { return "SortItemsByValuePerWeightPreprocessor"; }
};

class SortItemsByWeightPreprocessor : public ItemsPreprocessor
{
public:
  vector<Item> preprocess(vector<Item> items)
  {
    sort(items.begin(), items.end(), compareByWeight);
    return items;
  }

  string name() { return "SortItemsByWeightPreprocessor"; }
};

class SortItemsByValuePreprocessor : public ItemsPreprocessor
{
public:
  vector<Item> preprocess(vector<Item> items)
  {
    sort(items.begin(), items.end(), compareByValue);
    return items;
  }

  string name() { return "SortItemsByValuePreprocessor"; }
};

class RandomShuffleItemsPreprocessor : public ItemsPreprocessor
{
public:
  vector<Item> preprocess(vector<Item> items)
  {
    std::random_shuffle(items.begin(), items.end());
    return items;
  }

  string name() { return "RandomShuffleItemsPreprocessor"; }
};

bool compareById(const Item& lhs, const Item& rhs)
{
  return lhs.id < rhs.id;
}

void readInput(int& capacity, vector<Item>* items)
{
  int itemsNumber;
  cin >> itemsNumber >> capacity;
  items->resize(itemsNumber);
  for (int i = 0; i < itemsNumber; ++i)
  {
    int& value = (*items)[i].value;
    int& weight = (*items)[i].weight;
    cin >> value >> weight;
    (*items)[i].id = i;
  }
}

void printDataStatistics(int capacity, const vector<Item>& items)
{
  cerr << "----------------" << endl;
  cerr << "Data statistics:" << endl;
  cerr << "----------------" << endl;

  cerr << "Capacity equals " << capacity << endl;
  cerr << "Total number of items: " << items.size() << endl;

  {
    int totalWeight = 0;
    int totalValue = 0;
    for (int i = 0; i < items.size(); ++i)
    {
      totalValue += items[i].value;
      totalWeight += items[i].weight;
    }

    cerr << "Total value: " << totalValue << endl;
    cerr << "Total weight: " << totalWeight << endl;

    cerr << "Average value: " << double(totalValue) / totalWeight << endl;
    cerr << "Average weight: " << double(totalWeight) / items.size() << endl;
  }

  {
    int tooHeavyItemsNumber = 0;
    for (int i = 0; i < items.size(); ++i)
    {
      if (items[i].weight > capacity)
      {
        ++tooHeavyItemsNumber;
      }
    }
    cerr << "Too heavy items number: " << tooHeavyItemsNumber << endl;
  }

  // Weight distribution
  {
    int rangesNumber = 9;
    int weightDensity[rangesNumber];
    memset(weightDensity, 0, sizeof(weightDensity));
    int pow10 = 1;
    for (int i = 0; i < items.size(); ++i)
    {
      int weight = items[i].weight;
      pow10 = 1;
      for (int j = 0; j < rangesNumber; ++j)
      {
        if (weight <= pow10 * 10)
        {
          ++weightDensity[j];
        }
        pow10 *= 10;
      }
    }
    cerr << "Weight distribution: " << endl;
    pow10 = 1;
    for (int i = 0; i < rangesNumber; ++i)
    {
      int itemsNumber = weightDensity[i];
      if (i > 0)
      {
        itemsNumber -= weightDensity[i - 1];
      }
      cerr << "Items ith weight in range[" << pow10 << ", " << pow10 * 10 << "]: " << itemsNumber << endl;
      pow10 *= 10;
    }
  }
}

int calculateValue(int capacity, const vector<Item>& items, const vector<int>& takenList)
{
  int totalWeight = 0;
  int totalValue = 0;
  for (const int& id : takenList)
  {
    totalWeight += items[id].weight;
    totalValue += items[id].value;
  }
  if (totalWeight > capacity)
  {
    throw std::logic_error("totalWeight > capacity");
  }
  return totalValue;
}

class KnapsackSolver
{
public:
  KnapsackSolver(): bestTotalValue(0) {}

  void init(int capacity, const vector<Item>& items, int bestTotalValue)
  {
    this->capacity = capacity;
    this->items.assign(items.begin(), items.end());
    this->bestTotalValue = bestTotalValue;
    for (int i = 0; i < items.size(); ++i)
    {
      idToItem[items[i].id] = items[i];
    }
  }

  virtual ~KnapsackSolver()
  {
  }

  virtual string name() { return "KnapsackSolver"; }

  virtual int solve(vector<int>* takenList) = 0;

  void filterItems(const vector<int> takenList)
  {
    std::unordered_set<int> takenItemsSet;
    for (const int id : takenList)
    {
      capacity -= idToItem[id].weight;
      takenItemsSet.insert(id);
    }
    vector<Item> nextItems;
    for (int i = 0; i < items.size(); ++i)
    {
      if (takenItemsSet.find(items[i].id) != takenItemsSet.end())
      {
        continue;
      }
      if (items[i].weight <= capacity)
      {
        nextItems.push_back(items[i]);
      }
    }
    items = nextItems;
  }

protected:
  int capacity;
  vector<Item> items;
  int bestTotalValue;
  std::unordered_map<int, Item> idToItem;
};

class DynamicProgrammingKnapsackSolver : public KnapsackSolver
{
public:
  DynamicProgrammingKnapsackSolver(int maxAllowedProblemSize = DEFAULT_MAX_ALLOWED_PROBLEM_SIZE):
    maxAllowedProblemSize(maxAllowedProblemSize) {}

  virtual string name() { return "DynamicProgrammingKnapsackSolver"; }

  int solve(vector<int>* takenList)
  {
    if (capacity == 0 || items.size() == 0)
    {
      return 0;
    }

    takenList->clear();
    int allowedItemsNumber = items.size();
    if (capacity * 1ll * items.size() > maxAllowedProblemSize)
    {
      allowedItemsNumber = maxAllowedProblemSize / capacity;
    }

    cerr << "Max allowed problem size: " << maxAllowedProblemSize << endl;
    cerr << "Allowed items number: " << allowedItemsNumber << endl;

    int totalValue = solve(capacity, allowedItemsNumber, takenList);

    filterItems(*takenList);

    if (items.size() != 0)
    {
      std::vector<int> nextTakenList;
      totalValue += solve(&nextTakenList);
      for (int id : nextTakenList)
      {
        takenList->push_back(id);
      }
    }
    return totalValue;
  }
  static const int DEFAULT_MAX_ALLOWED_PROBLEM_SIZE = 300000000;

private:
  int solve(int capacity, int itemsNumber, vector<int>* takenList)
  {
    // Computing dynamic programming table
    vector< vector<int> > bestPickValues(itemsNumber + 1, vector<int>(capacity + 1, 0));

    for (int currentItem = 1; currentItem <= itemsNumber; ++currentItem)
    {
      for (int currentCapacity = 0; currentCapacity <= capacity; ++currentCapacity)
      {
        int& bestPickValue = bestPickValues[currentItem][currentCapacity];
        bestPickValue = bestPickValues[currentItem - 1][currentCapacity];

        int itemWeight = items[currentItem - 1].weight;
        if (itemWeight <= currentCapacity)
        {
          int previousValue = 0; 
          previousValue = bestPickValues[currentItem - 1][currentCapacity - itemWeight];
          bestPickValue = std::max(bestPickValue, items[currentItem - 1].value + previousValue);
        }
      }
    }

    // Backtracking
    int currentCapacity = capacity;
    int currentItem = itemsNumber;
    for (int i = 0; i < itemsNumber; ++i)
    {
      if (bestPickValues[currentItem][currentCapacity] != bestPickValues[currentItem - 1][currentCapacity]) {
        takenList->push_back(items[currentItem - 1].id);
        currentCapacity -= items[currentItem - 1].weight;
      }
      --currentItem;
    }

    return bestPickValues[itemsNumber][capacity];
  }

  int maxAllowedProblemSize = DEFAULT_MAX_ALLOWED_PROBLEM_SIZE;
};

class GreedyKnapsackSolver : public KnapsackSolver
{
public:
  GreedyKnapsackSolver() {}

  virtual string name() { return "GreedyKnapsackSolver"; }

  int solve(vector<int>* takenList)
  {
    int totalValue = 0;
    for (int i = 0; i < items.size(); ++i)
    {
      if (capacity > items[i].weight)
      {
        capacity -= items[i].weight;
        totalValue += items[i].value;
        takenList->push_back(items[i].id);
      }
    }
    return totalValue;
  }
};

class SplitKnapsackSolver : public KnapsackSolver
{
public:
  SplitKnapsackSolver(int splitsNumber = 2): splitsNumber(splitsNumber) {}

  virtual string name() { return "SplitKnapsackSolver"; }

  int solve(vector<int>* takenList)
  {
    int itemsLeft = items.size();
    int splitsLeft = splitsNumber;
    int nextItemPosition = 0;
    int totalValue = 0;
    for (int i = 0; i < splitsNumber; ++i)
    {
      int splitSize = itemsLeft / splitsLeft;
      --splitsLeft;
      itemsLeft -= splitSize;

      vector<Item> splitItems(items.begin() + nextItemPosition, 
                              items.begin() + nextItemPosition + splitSize);
  
      nextItemPosition += splitSize;
      sort(splitItems.begin(), splitItems.end(), compareByValuePerWeight);

      DynamicProgrammingKnapsackSolver 
        solver(DynamicProgrammingKnapsackSolver::DEFAULT_MAX_ALLOWED_PROBLEM_SIZE / splitsNumber);
      solver.init(capacity / splitsNumber, splitItems, bestTotalValue);
      vector<int> solverTakenList;
      totalValue += solver.solve(&solverTakenList);
      for (int id : solverTakenList)
      {
        takenList->push_back(id);
      }
    }

    filterItems(*takenList);
    sort(items.begin(), items.end(), compareByValuePerWeight);

    if (items.size() != 0)
    {
      std::vector<int> nextTakenList;
      DynamicProgrammingKnapsackSolver solver;
      solver.init(capacity, items, bestTotalValue);
      totalValue += solver.solve(&nextTakenList);
      for (int id : nextTakenList)
      {
        takenList->push_back(id);
      }
    }
    return totalValue;
  }
private:
  int splitsNumber;
};

class BranchAndBoundKnapsackSolver : public KnapsackSolver
{
public:
  BranchAndBoundKnapsackSolver(): currentIteration(0) {}

  virtual string name() { return "BranchAndBoundKnapsackSolver"; }

  int solve(vector<int>* takenList)
  {
    sortedItems = items;
    sort(sortedItems.begin(), sortedItems.end(), compareByValuePerWeight);
    vector<int> currentTakenList;
    takenList->clear();
    double bestEstimation = calculateBestEstimation(capacity);
    cerr << "Best estimation: " << bestEstimation << endl;
    solve(capacity, 0, 0, bestEstimation, &currentTakenList, takenList);
    if (takenList->size() == 0)
    {
      return 0;
    }
    else
    {
      return bestTotalValue;
    }
  }
private:

  vector<Item> sortedItems;
  std::unordered_set<int> processedSet;

  double calculateBestEstimation(int currentCapacity)
  {
    double totalValue = 0;
    for (int i = 0; i < sortedItems.size(); ++i)
    {
      if (processedSet.find(sortedItems[i].id) != processedSet.end())
      {
        continue;
      }
      if (sortedItems[i].weight <= currentCapacity)
      {
        currentCapacity -= sortedItems[i].weight;
        totalValue += sortedItems[i].value;
      }
      else
      {
        totalValue += sortedItems[i].value * (double(currentCapacity) / sortedItems[i].weight);
        break;
      }
    }
    return totalValue;
  }

  void solve(int currentCapacity, int currentItem, int currentTotalValue, 
            double bestExpectedAddition,
            vector<int>* currentTakenList, vector<int>* takenList)
  {
    ++currentIteration;
    if (currentIteration > MAX_ITERATIONS_NUMBER)
    {
      return;
    }
    // cerr << "currentCapacity " << currentCapacity << endl;
    // cerr << "currentItem " << currentItem << endl;
    // cerr << "currentTotalValue " << currentTotalValue << endl;
    // cerr << "bestExpectedAddition " << bestExpectedAddition << endl;
    if (currentTotalValue > bestTotalValue)
    {
      cerr << "Found better! " << currentTotalValue << endl;
      bestTotalValue = currentTotalValue;
      takenList->assign(currentTakenList->begin(), currentTakenList->end());
    }
    if (currentTotalValue + bestExpectedAddition <= bestTotalValue)
    {
      return;
    }
    if (currentItem == items.size())
    {
      return;
    }

    processedSet.insert(items[currentItem].id);
    if (items[currentItem].weight <= currentCapacity)
    {
      int nextCapacity = currentCapacity - items[currentItem].weight;
      int nextTotalValue = currentTotalValue + items[currentItem].value;
      double nextBestExpectedAddition = calculateBestEstimation(nextCapacity);
      currentTakenList->push_back(items[currentItem].id);

      solve(nextCapacity, currentItem + 1, nextTotalValue, nextBestExpectedAddition,
            currentTakenList, takenList);

      currentTakenList->pop_back();
    }
    double nextBestExpectedAddition = calculateBestEstimation(currentCapacity);
    solve(currentCapacity, currentItem + 1, currentTotalValue, nextBestExpectedAddition,
          currentTakenList, takenList);
    processedSet.erase(items[currentItem].id);
  }

  double bestDensity;
  int currentIteration = 0;
  const int MAX_ITERATIONS_NUMBER = 1000000000;
};

void findBestAnswer(int capacity, const vector<Item>& items, vector<int>* takenList)
{
  cerr << "-------------------" << endl;
  cerr << "Finding best answer" << endl;
  cerr << "-------------------" << endl;

  vector< std::pair<KnapsackSolver*, ItemsPreprocessor*> > solverPairs;

  solverPairs.push_back(std::make_pair(new GreedyKnapsackSolver, new SortItemsByValuePerWeightPreprocessor));
  solverPairs.push_back(std::make_pair(new GreedyKnapsackSolver, new SortItemsByValuePreprocessor));
  // for (int i = 0; i < 10; ++i)
  // {
  //   solverPairs.push_back(std::make_pair(new GreedyKnapsackSolver, new RandomShuffleItemsPreprocessor));
  // }
  solverPairs.push_back(std::make_pair(new DynamicProgrammingKnapsackSolver, new SortItemsByValuePerWeightPreprocessor));
  /* solverPairs.push_back(std::make_pair(new DynamicProgrammingKnapsackSolver, new SortItemsByValuePreprocessor)); */
  /* solverPairs.push_back(std::make_pair(new DynamicProgrammingKnapsackSolver, new RandomShuffleItemsPreprocessor)); */
  // for (int i = 0; i < 10; ++i)
  // {
  //   solverPairs.push_back(std::make_pair(new DynamicProgrammingKnapsackSolver, new RandomShuffleItemsPreprocessor));
  // }

  solverPairs.push_back(std::make_pair(new SplitKnapsackSolver(2), new RandomShuffleItemsPreprocessor));

  solverPairs.push_back(std::make_pair(new BranchAndBoundKnapsackSolver, new SortItemsByValuePerWeightPreprocessor));
  // solverPairs.push_back(std::make_pair(new BranchAndBoundKnapsackSolver, new SortItemsByValuePreprocessor));
  // solverPairs.push_back(std::make_pair(new BranchAndBoundKnapsackSolver, new SortItemsByWeightPreprocessor));
  // for (int i = 0; i < 10; ++i)
  // {
  //   solverPairs.push_back(std::make_pair(new BranchAndBoundKnapsackSolver, new RandomShuffleItemsPreprocessor));
  // }

  int bestTotalValue = 0;
  for (int i = 0; i < solverPairs.size(); ++i)
  {
    KnapsackSolver* solver = solverPairs[i].first;
    ItemsPreprocessor* preprocessor = solverPairs[i].second;

    cerr << "(" << solver->name() << ", " << preprocessor->name() << ") started" << endl;

    vector<int> solverTakenList;
    solver->init(capacity, preprocessor->preprocess(items), bestTotalValue);
    int solverTotalValue = solver->solve(&solverTakenList);

    if (solverTotalValue > bestTotalValue)
    {
      bestTotalValue = solverTotalValue;
      takenList->assign(solverTakenList.begin(), solverTakenList.end());
    }

    cerr << "Total value: " << solverTotalValue << endl;
    cerr << "(" << solver->name() << ", " << preprocessor->name() << ") finished" << endl << endl;
  }
}

void outputAnswer(int capacity, const vector<Item>& items, const vector<int>& takenList)
{
  int totalValue = calculateValue(capacity, items, takenList);
  cout << totalValue << " " << 1 << endl;

  vector<char> taken(items.size());
  for (const int& id : takenList)
  {
    taken[id] = true;
  }

  for (int i = 0; i < taken.size(); ++i)
  {
    cout << int(taken[i]) << " ";
  }
  cout << endl;
}

int main(int argc, char** argv)
{
  if (argc > 2) 
  {
    cerr << "Usage: solve [inputFile]" << endl;
    return 1;
  }
  if (argc == 2)
  {
    freopen(argv[1], "r", stdin);
  }

  int capacity;
  vector<Item> items;
  readInput(capacity, &items);

  printDataStatistics(capacity, items);

  vector<int> takenList;
  findBestAnswer(capacity, items, &takenList);

  outputAnswer(capacity, items, takenList);

  return 0;
}

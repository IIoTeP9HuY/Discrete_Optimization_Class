#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <vector>
#include <algorithm>
#include <functional>
#include <stdexcept>
#include <numeric>
#include <memory.h>

using std::cerr;
using std::cin;
using std::cout;
using std::endl;
using std::vector;

struct Item
{
  int value, weight;
  int id;
};

bool compareByValuePerWeight(const Item& lhs, const Item& rhs)
{
  return double(lhs.value) / lhs.weight > double(rhs.value) / rhs.weight;
}

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

class KnapsackSolver
{
public:
  KnapsackSolver(int capacity, const vector<Item>& items): 
    capacity(capacity), 
    items(items) 
  {
  }

  virtual ~KnapsackSolver()
  {
  }

  virtual int solve(vector<int>* takenList) = 0;

protected:
  int capacity;
  vector<Item> items;
};

class DynamicProgrammingKnapsackSolver : public KnapsackSolver
{
public:
  DynamicProgrammingKnapsackSolver(int capacity, const vector<Item>& items): KnapsackSolver(capacity, items)
  {
  }

  int solve(vector<int>* takenList)
  {
    if (capacity == 0 || items.size() == 0)
    {
      return 0;
    }

    sort(items.begin(), items.end(), compareByValuePerWeight);

    int allowedItemsNumber = items.size();
    if (capacity * 1ll * items.size() > MAX_ALLOWED_PROBLEM_SIZE)
    {
      allowedItemsNumber = MAX_ALLOWED_PROBLEM_SIZE / capacity;
    }

    cerr << "Allowed items number: " << allowedItemsNumber << endl;

    return solve(capacity, allowedItemsNumber, takenList);
  }

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
      if (bestPickValues[currentItem][currentCapacity] != bestPickValues[currentItem - 1][currentCapacity])
      {
        takenList->push_back(items[currentItem - 1].id);
        currentCapacity -= items[currentItem - 1].weight;
      }
      --currentItem;
    }

    return bestPickValues[itemsNumber][capacity];
  }

  const int MAX_ALLOWED_PROBLEM_SIZE = 10000000;
};

class GreedyKnapsackSolver : public KnapsackSolver
{
public:
  GreedyKnapsackSolver(int capacity, const vector<Item>& items): KnapsackSolver(capacity, items)
  {
  }

  int solve(vector<int>* takenList)
  {
    sort(items.begin(), items.end(), compareByValuePerWeight);

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

void findBestAnswer(int capacity, const vector<Item>& items, vector<int>* takenList)
{
  cerr << "-------------------" << endl;
  cerr << "Finding best answer" << endl;
  cerr << "-------------------" << endl;

  cerr << "GreedyKnapsackSolver started" << endl;
  GreedyKnapsackSolver greedySolver(capacity, items);
  vector<int> greedyTakenList;
  int greedyTotalValue = greedySolver.solve(&greedyTakenList);
  cerr << "GreedyKnapsackSolver finished" << endl << endl;

  cerr << "DynamicProgrammingKnapsackSolver started" << endl;
  DynamicProgrammingKnapsackSolver dpSolver(capacity, items);
  vector<int> dpTakenList;
  int dpTotalValue = dpSolver.solve(&dpTakenList);
  cerr << "DynamicProgrammingKnapsackSolver finished" << endl << endl;

  cerr << "Greedy total value: " << greedyTotalValue << endl;
  cerr << "Dynamic programming total value: " << dpTotalValue << endl;

  if (greedyTotalValue > dpTotalValue)
  {
    takenList->assign(greedyTakenList.begin(), greedyTakenList.end());
  }
  else
  {
    takenList->assign(dpTakenList.begin(), dpTakenList.end());
  }
}

void outputAnswer(int capacity, const vector<Item>& items, const vector<int>& takenList)
{
  int totalValue = calculateValue(capacity, items, takenList);
  cout << totalValue << " " << 0 << endl;

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

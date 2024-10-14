package edu.grinnell.csc207.util;

import static java.lang.reflect.Array.newInstance;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @param <K> the key type
 * @param <V> the value type
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V>[] pairs;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({ "unchecked" })
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(),
        DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   *
   * @return a new copy of the array
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K, V> second = new AssociativeArray<K, V>();
    for (int i = 0; i < this.size; i++) {
      if (second.size == this.pairs.length) {
        second.expand();
      } // expand array when elements amount equal max size
      try {
        second.set(this.pairs[i].key, this.pairs[i].val);
      } catch (NullKeyException a) {
      } // end for handling exception
    } // iterate through original array and set new array
    return second;
  } // clone()

  /**
   * Convert the array to a string.
   *
   * @return a string of the form "{Key0:Value0, Key1:Value1, ... KeyN:ValueN}"
   */
  public String toString() {
    String str = "{";
    for (int i = 0; i < this.size; i++) {
      if (i == this.size - 1) {
        str = str + this.pairs[i].key + ":" + this.pairs[i].val;
      } else {
        str = str + this.pairs[i].key + ":" + this.pairs[i].val + ", ";
      } // if last element, only print pair, else print with ','
    } // go through pairs of array
    return str + "}";
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   *
   * @param key
   *              The key whose value we are seeting.
   * @param value
   *              The value of that key.
   *
   * @throws NullKeyException
   *                          If the client provides a null key.
   */
  public void set(K key, V value) throws NullKeyException {
    if (this.size == this.pairs.length) {
      this.expand();
    } // expands arary if amount of elements is same as max array
    if (key == null) {
      throw new NullKeyException();
    } // throw exception if key is null

    if (hasKey(key)) {
      for (int i = 0; i < this.size; i++) {
        if (pairs[i].key.equals(key)) {
          this.pairs[i].val = value;
          return;
        } // return if key is found
      } // iterate to find key
    } else {
      this.pairs[this.size] = new KVPair<>();
      this.pairs[this.size].key = key;
      this.pairs[this.size].val = value;
      this.size++;
      return;
    } // checks if key is found, if so change val, if not insert
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key
   *            A key
   *
   * @return
   *         The corresponding value
   *
   * @throws KeyNotFoundException
   *                              when the key is null or does not appear in the
   *                              associative array.
   */
  public V get(K key) throws KeyNotFoundException {
    if (this.hasKey(key)) {
      int index = this.find(key);
      return this.pairs[index].val;
    } // if key is found, return index
    throw new KeyNotFoundException();
  } // get(K)

  /**
   * Determine if key appears in the associative array. Should
   * return false for the null key, since it cannot appear.
   *
   * @param key
   *            The key we're looking for.
   *
   * @return true if the key appears and false otherwise.
   */
  public boolean hasKey(K key) {
    for (int i = 0; i < this.size; i++) {
      if (pairs[i].key.equals(key)) {
        return true;
      } // if key is found, return true
    } // iterate through pairs to find key
    return false;
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.
   *
   * @param key
   *            The key to remove.
   */
  public void remove(K key) {
    if (this.hasKey(key)) {
      int index = -1;
      for (int i = 0; i < this.size; i++) {
        if (pairs[i].key.equals(key)) {
          index = i;
          this.size--;
        } // if key is found, set index and decrement size for removal
      } // iterates through array to find key
      if (index > -1) {
        for (int shift = index; shift < this.size(); shift++) {
          this.pairs[shift].key = this.pairs[shift + 1].key;
          this.pairs[shift].val = this.pairs[shift + 1].val;
        } // iterates through array to shift
      } // if index has been set (> -1) should shift pairs down from that point
      this.pairs[this.size] = null;
    } // checks if there is a key, then shifts down elements from it and removes duplicate
  } // remove(K)

  /**
   * Determine how many key/value pairs are in the associative array.
   *
   * @return The number of key/value pairs in the array.
   */
  public int size() {
    return this.size;
  } // size()

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  void expand() {
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   *
   * @param key
   *            The key of the entry.
   *
   * @return
   *         The index of the key, if found.
   *
   * @throws KeyNotFoundException
   *                              If the key does not appear in the associative
   *                              array.
   */
  int find(K key) throws KeyNotFoundException {
    for (int i = 0; i < this.size; i++) {
      if (pairs[i].key.equals(key)) {
        return i;
      } // end check if key is in array
    } // end loop to check each pair in array
    throw new KeyNotFoundException();
  } // find(K)

} // class AssociativeArray

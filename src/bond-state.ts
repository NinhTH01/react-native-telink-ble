/**
 * Device bond state
 *
 * @enum {number}
 */
export enum BondState {
  /**
   * This device is not bonded
   *
   * @type {BondState.BOND_NONE}
   */
  BOND_NONE = 10,

  /**
   * This device is bonding
   *
   * @type {BondState.BOND_BONDING}
   */
  BOND_BONDING = 11,

  /**
   * This device is bonded
   *
   * @type {BondState.BOND_BONDED}
   */
  BOND_BONDED = 12,
}

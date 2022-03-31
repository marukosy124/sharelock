class AddUniqueLockAccess < ActiveRecord::Migration[7.0]
  def change
    add_index :lock_accesses, %i[lock_id user_id], unique: true
  end
end

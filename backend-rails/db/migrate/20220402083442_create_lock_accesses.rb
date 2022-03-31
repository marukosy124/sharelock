class CreateLockAccesses < ActiveRecord::Migration[7.0]
  def change
    create_table :lock_accesses do |t|
      t.references :lock, null: false, foreign_key: true
      t.references :user, type: :string, null: false, foreign_key: true
      t.integer :permission, default: 0

      t.timestamps
    end
  end
end

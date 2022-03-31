class CreateLockHistories < ActiveRecord::Migration[7.0]
  def change
    create_table :lock_histories do |t|
      t.references :lock, null: false, foreign_key: true
      t.references :user, type: :string, null: false, foreign_key: true
      t.timestamp :accessed_at

      t.timestamps
    end
  end
end

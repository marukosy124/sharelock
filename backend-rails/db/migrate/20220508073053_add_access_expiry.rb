class AddAccessExpiry < ActiveRecord::Migration[7.0]
  def change
    change_table :lock_accesses do |t|
      t.timestamp :expired_at, null: true
    end
  end
end

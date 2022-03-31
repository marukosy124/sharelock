class CreateLocks < ActiveRecord::Migration[7.0]
  def change
    create_table :locks do |t|
      t.string :name
      t.string :private_key

      t.timestamps
    end
  end
end

class Lock < ApplicationRecord
  has_many :lock_accesses, :dependent => :destroy
  has_many :users, through: :lock_accesses
  has_many :lock_histories, :dependent => :destroy

  attr_accessor :token
end

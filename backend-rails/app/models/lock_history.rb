class LockHistory < ApplicationRecord
  belongs_to :lock
  belongs_to :user
end

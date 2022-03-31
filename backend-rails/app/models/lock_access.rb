class LockAccess < ApplicationRecord
  belongs_to :lock
  belongs_to :user

  # only return records that is not expired
  default_scope -> { where(:expired_at => nil).or(where("expired_at > ?", Time.now)) }
  # default_scope -> { where("expired_at IS NULL OR expired_at > ?", Time.now) }

  enum :permission, { user: 0, manager: 10, owner: 20 }
end

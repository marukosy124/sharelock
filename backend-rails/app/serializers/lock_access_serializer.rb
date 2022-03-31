class LockAccessSerializer < ActiveModel::Serializer
  attributes :id, :user, :permission, :expired_at

  belongs_to :user
end

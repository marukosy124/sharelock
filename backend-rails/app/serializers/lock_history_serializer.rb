class LockHistorySerializer < ActiveModel::Serializer
  attributes :id, :user, :accessed_at

  belongs_to :user
end

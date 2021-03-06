package de.hatoka.group.internal.business;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import de.hatoka.group.capi.business.GroupBO;
import de.hatoka.group.capi.business.GroupBusinessFactory;
import de.hatoka.group.capi.business.MemberBO;
import de.hatoka.group.capi.dao.GroupDao;
import de.hatoka.group.capi.dao.MemberDao;
import de.hatoka.group.capi.entities.GroupPO;
import de.hatoka.group.capi.entities.MemberPO;

public class GroupBOImpl implements GroupBO
{
    private GroupPO groupPO;
    private final GroupDao groupDao;
    private final MemberDao memberDao;
    private final GroupBusinessFactory factory;

    public GroupBOImpl(GroupPO groupPO, GroupDao groupDao,MemberDao memberDao, GroupBusinessFactory factory)
    {
        this.groupPO = groupPO;
        this.groupDao = groupDao;
        this.memberDao = memberDao;
        this.factory = factory;
    }

    @Override
    public String getOwner()
    {
        return groupPO.getOwnerRef();
    }

    @Override
    public void remove()
    {
        groupDao.remove(groupPO);
    }

    @Override
    public String getName()
    {
        return groupPO.getName();
    }

    @Override
    public MemberBO createMember(String userRef, String name)
    {
        MemberPO memberPO = memberDao.createAndInsert(groupPO, userRef, name);
        return factory.getMemberBO(memberPO);
    }

    @Override
    public Collection<MemberBO> getMembers()
    {
        return groupPO.getMembers().stream().map(factory::getMemberBO).collect(Collectors.toList());
    }

    @Override
    public String getID()
    {
        return groupPO.getId();
    }

    @Override
    public MemberBO getMember(String userRef)
    {
        Optional<MemberBO> optional = groupPO.getMembers().stream().filter(m -> m.getUserRef().equals(userRef)).findAny().map(factory::getMemberBO);
        return optional.isPresent() ? optional.get() : null;
    }

    @Override
    public int hashCode()
    {
        return groupPO.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupBOImpl other = (GroupBOImpl)obj;
        if (groupPO == null)
        {
            if (other.groupPO != null)
                return false;
        }
        else if (!groupPO.equals(other.groupPO))
            return false;
        return true;
    }

    @Override
    public boolean isMember(String userRef)
    {
        return getMember(userRef) != null;
    }


}

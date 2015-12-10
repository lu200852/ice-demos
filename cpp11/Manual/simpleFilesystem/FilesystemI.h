// **********************************************************************
//
// Copyright (c) 2003-2015 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#ifndef __FilesystemI_h__
#define __FilesystemI_h__

#include <Ice/Ice.h>
#include <Filesystem.h>

#ifdef _MSC_VER
#   pragma warning(push)
#   pragma warning(disable:4250) // ... : inherits ... via dominance
#endif

namespace Filesystem
{
    class DirectoryI;

    class NodeI : virtual public Node, public std::enable_shared_from_this<NodeI>
    {
    public:
        
        NodeI(const NodeI&) = delete;            // Copy forbidden
        void operator=(const NodeI&) = delete;   // Assignment forbidden

        virtual std::string name(const Ice::Current&);
        NodeI(std::string, std::shared_ptr<DirectoryI>);
        void activate(const std::shared_ptr<Ice::ObjectAdapter>&);

    private:

        std::string _name;
        Ice::Identity _id;
        std::shared_ptr<DirectoryI> _parent;
    };

    class FileI : public File, public NodeI
    {
    public:

        virtual Lines read(const Ice::Current&);
        virtual void write(Lines, const Ice::Current& = Ice::Current());
        
        FileI(std::string, std::shared_ptr<DirectoryI>);

    private:

        Lines _lines;
    };

    class DirectoryI : public Directory, public NodeI
    {
    public:

        virtual NodeSeq list(const Ice::Current&);
        DirectoryI(std::string, std::shared_ptr<DirectoryI>);
        void addChild(const std::shared_ptr<Filesystem::NodePrx>&);

    private:

        Filesystem::NodeSeq _contents;
    };
}

#ifdef _MSC_VER
#   pragma warning(pop)
#endif

#endif